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
 */
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    static final String TOKEN_KEY = "access_token";
    static final String USER_ID = "user_id";
    static final String AGENT_ID = "user_agent_no";
    static final String LOGIN_ACTION_URI = "/login";
    static final String LOGOUT_ACTION_URI = "/logout";
    static final String RES_COMPANY_KEY = "tenant_id";
    static final String[] STATIC_RESOURCE = {".js", ".css", ".png", ".jpg", ".jpeg", ".img", ".ico", ".mp4", ".mp3", ".wav"};

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
        if(request.getMethod().equals(RequestMethod.OPTIONS.name())){
            log.debug("OPTION请求，跳过");
            return false;
        }
        if(is3partRequest(request.getRequestURI())){
            log.debug("第三方URL请求，跳过 uri = {}", request.getRequestURI());
            return false;
        }
        return !isStaticResource(request.getRequestURI());
    }

    private boolean isStaticResource(String uri) {
        log.debug("静态资源跳过权限过滤器 URI = {}", uri);
        for (String res : STATIC_RESOURCE) {
            if(uri.endsWith(res) || uri.endsWith(res.toUpperCase())){
                return true;
            }
        }
        if(uri.startsWith("/reporter")){
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        log.debug("*****************AccessFilter run start*****************");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();

        log.info("网关接收请求 {} URL {} method = {}", request.getMethod(), requestURI, request.getMethod());

        //获取传来的参数accessToken
        String tokenString = request.getHeader(TOKEN_KEY) != null ? request.getHeader(TOKEN_KEY) : request.getParameter(TOKEN_KEY);

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
            ctx.setResponseStatusCode(403);
            ctx.setResponseBody("{\"result\":\"accessToken is invalid!\"}");
            log.debug("*****************AccessFilter run end*****************");
            return null;
        }

        String method = request.getMethod().toLowerCase();
        if( !service.validAuthoriy(tokenData, method + "@"+requestURI.substring(1))){
            log.warn("access token is invalid can access URI {}", requestURI);
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            ctx.setResponseBody("{\"result\":\"Permission denied\"}");
            log.debug("*****************AccessFilter run end*****************");
            return null;
        }

        ctx.addZuulRequestHeader(RES_COMPANY_KEY,tokenData.getStr("tenantId"));
        ctx.addZuulRequestHeader(USER_ID, tokenData.getStr("id"));
        ctx.addZuulRequestHeader(AGENT_ID, tokenData.getStr("agentNo"));


        log.info("网关接收请求验证通过 {} URL {}", request.getMethod(), requestURI);
        log.debug("*****************AccessFilter run end*****************");
        return null;
    }

    /**
     * /urule 因为设计器，使用嵌套 iFrame vue的方式，内层的 vue 部分无法添加请求头，决定针对所有这个路径的请求都放行。
     * /wordcloud只用于测试和开发
     *
     * @param uri
     * @return
     */
    private boolean is3partRequest(String uri) {
        if(uri.startsWith("/urule")){
            return true;
        }else if(uri.startsWith("/wordcloud")){
            return true;
        } else{
            return false;
        }
    }


}
