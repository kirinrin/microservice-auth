package me.kirinrin.zuul;

import cn.hutool.json.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kirinrin
 *
 * 测试请求
 * curl -H "as_tenant_id:wechat.cas-online.com" -H "access_token:admin:BQBVCl0NUwQVVwRWARwGA1IBFANcXl1DC1RUCgxRVVcDVw0A" http://localhost:7070/system/x
 */
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    static final String TOKEN_KEY = "access_token";
    static final String USER_ID = "user_id";
    static final String AGENT_ID = "user_agent_no";
    static final String LOGIN_ACTION_URI = "/login";
    static final String LOGOUT_ACTION_URI = "/logout";
    static final String REPORTER_URI = "/reporter";
    static final String URULE_URI = "/urule";
    static final String WORDCLOUD_URI = "/wordcloud";
    static final String CLOUD_MANAGEMENT_URI = "/cloud-management";
    static final String RES_COMPANY_KEY = "tenant_id";
    static final String[] STATIC_RESOURCE = {".js", ".css", ".png", ".jpg", ".jpeg", ".img", ".ico", ".mp4", ".mp3", ".wav"};
    static final String AS_TENANT_ID = "as_tenant_id";
    static final String AUTH_SPLIT_CHAT= "@";


    final
    TokenAuthorityService service;

    public AccessFilter(TokenAuthorityService service) {
        this.service = service;
    }

    @Override
    public String filterType() {
        //前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        //共享RequestContext，上下文对象
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        log.debug("判断请求是否执行权限过滤器 URI = {}", request.getRequestURI());
        //不需要token校验的URL
        if (LOGIN_ACTION_URI.equalsIgnoreCase(request.getRequestURI()) || LOGOUT_ACTION_URI.equalsIgnoreCase(request.getRequestURI())) {
            //登录接口不需要校验
            return false;
        }
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            log.debug("OPTION请求，跳过");
            return false;
        }
        if (is3partRequest(request.getRequestURI())) {
            log.debug("第三方URL请求，跳过 uri = {}", request.getRequestURI());
            return false;
        }
        return !isStaticResource(request.getRequestURI());
    }

    private boolean isStaticResource(String uri) {
        log.debug("静态资源跳过权限过滤器 URI = {}", uri);
        for (String res : STATIC_RESOURCE) {
            if (uri.endsWith(res) || uri.endsWith(res.toUpperCase())) {
                return true;
            }
        }
        if (uri.startsWith(REPORTER_URI)) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        log.debug("*****************AccessFilter run start*****************");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestUri = request.getRequestURI();

        log.info("网关接收请求 {} URL {} method = {}", request.getMethod(), requestUri, request.getMethod());

        //获取传来的参数accessToken
        String tokenString = request.getHeader(TOKEN_KEY) != null ? request.getHeader(TOKEN_KEY) : request.getParameter(TOKEN_KEY);
        String asTenantId = request.getHeader(AS_TENANT_ID) != null ? request.getHeader(AS_TENANT_ID) : request.getParameter(AS_TENANT_ID);

        if (tokenString == null) {
            log.warn("access token is empty!");
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{\"result\":\"accessToken is empty!\"}");
            log.debug("*****************AccessFilter run end*****************");
            return null;
        }

        JSONObject tokenData = service.getTokenData(tokenString);
        log.debug("token = {} tokenData = {}", tokenString, tokenData);
        if (tokenData == null) {
            log.warn("access token is invalid!");
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{\"result\":\"accessToken is invalid!\"}");
            log.debug("*****************AccessFilter run end*****************");
            return null;
        }
        String tenantId = tokenData.getStr("tenantId");
        if (isCloudManagementRequest(requestUri)){
            log.info("访问 {} 跳过权限认证", requestUri);
        }else if(isChildDomainAccess(tenantId, asTenantId)) {
            log.info("是子域访问放行所有请求，不进行权限验证 tenantId = {}  as_tenant_id = {}", tenantId, asTenantId);

        }else{
            String method = request.getMethod().toLowerCase();
            if (!service.validAuthoriy(tokenData, method + AUTH_SPLIT_CHAT + requestUri.substring(1))) {
                log.warn("access token is invalid can access URI {}", requestUri);
                //过滤该请求，不往下级服务去转发请求，到此结束
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(403);
                ctx.setResponseBody("{\"result\":\"Permission denied\"}");
                log.debug("*****************AccessFilter run end*****************");
                return null;
            }
        }

        if (asTenantId != null){
            log.info("set as_tenant_id as tenant_id {} -> {}", asTenantId, tenantId);
            if (asTenantId.endsWith(tenantId)){
                log.info("校验通过，可以变更tenant_id");
                tenantId = asTenantId;
            }
        }

        ctx.addZuulRequestHeader(RES_COMPANY_KEY, tenantId);
        ctx.addZuulRequestHeader(USER_ID, tokenData.getStr("id"));
        ctx.addZuulRequestHeader(AGENT_ID, tokenData.getStr("agentNo"));


        log.info("网关接收请求验证通过 {} URL {}", request.getMethod(), requestUri);
        log.debug("*****************AccessFilter run end*****************");
        return null;
    }

    private boolean isCloudManagementRequest(String requestUri) {
        log.info("跳过 URI 权限认证判断", requestUri);
        return requestUri.startsWith(CLOUD_MANAGEMENT_URI);
    }


    private boolean isChildDomainAccess(String domain, String childDomain) {
        if (domain != null && childDomain != null && childDomain.endsWith(domain)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * /urule 因为设计器，使用嵌套 iFrame vue的方式，内层的 vue 部分无法添加请求头，决定针对所有这个路径的请求都放行。
     * /wordcloud只用于测试和开发
     *
     * @param uri
     * @return
     */
    private boolean is3partRequest(String uri) {
        if (uri.startsWith(URULE_URI)) {
            return true;
        } else if (uri.startsWith(WORDCLOUD_URI)) {
            return true;
        } else {
            return false;
        }
    }


}
