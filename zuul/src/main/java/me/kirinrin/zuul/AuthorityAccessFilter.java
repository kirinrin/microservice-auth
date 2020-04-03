package me.kirinrin.zuul;

import cn.hutool.json.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static me.kirinrin.zuul.Constants.CLOUD_MANAGEMENT_TENANT_ID;
import static me.kirinrin.zuul.RequestUtil.*;

/**
 * @author Kirinrin
 *
 * 测试请求
 * curl -H "as_tenant_id:wechat.cas-online.com" -H "access_token:admin:BQBVCl0NUwQVVwRWARwGA1IBFANcXl1DC1RUCgxRVVcDVw0A" http://localhost:7070/system/x
 */
@Component
@Slf4j
public class AuthorityAccessFilter extends ZuulFilter {

    final
    TokenAuthorityService service;

    public AuthorityAccessFilter(TokenAuthorityService service) {
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
        if (isLoginLogoutRequest(request.getRequestURI())) {
            log.debug("登录登出请求，跳过");
            return false;
        }
        if (isOptionRequest(request.getMethod())) {
            log.debug("OPTION请求，跳过");
            return false;
        }
        if (is3partRequest(request.getRequestURI())) {
            log.debug("第三方URL请求，跳过 uri = {}", request.getRequestURI());
            return false;
        }
        if (isStaticResource(request.getRequestURI())){
            log.debug("静态资源请求，跳过");
            return false;
        }
        return true;
    }

    @Override
    public Object run() {
        log.debug("*****************AccessFilter run start*****************");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestUri = request.getRequestURI();

        log.info("网关接收请求 {} URL {} method = {}", request.getMethod(), requestUri, request.getMethod());

        //获取传来的参数accessToken
        String tokenString = request.getHeader(Constants.TOKEN_KEY) != null ? request.getHeader(Constants.TOKEN_KEY) : request.getParameter(Constants.TOKEN_KEY);
        String asTenantId = request.getHeader(Constants.AS_TENANT_ID) != null ? request.getHeader(Constants.AS_TENANT_ID) : request.getParameter(Constants.AS_TENANT_ID);

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
        String tenantId = tokenData.getStr(Constants.TENANT_ID);
        if (isCloudManagementRequest(requestUri)){
            log.info("访问 {} 跳过权限认证但要求tenantId是主域的", requestUri);
            if (CLOUD_MANAGEMENT_TENANT_ID.equalsIgnoreCase(tenantId)){
                log.info("验证tenantId通过 {}", tenantId);
                return null;
            }

            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            ctx.setResponseBody("{\"result\":\"Access CloudManagement Permission denied wrong account domain\"}");
            log.debug("*****************AccessFilter run end*****************");
            return null;
        }else if(isChildDomainAccess(tenantId, asTenantId)) {
            log.info("是子域访问放行所有请求，不进行权限验证 tenantId = {}  as_tenant_id = {}", tenantId, asTenantId);

        }else{
            String method = request.getMethod().toLowerCase();
            if (!service.validAuthoriy(tokenData, method + Constants.AUTH_SPLIT_CHAT + requestUri.substring(1))) {
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

        ctx.addZuulRequestHeader(Constants.RES_COMPANY_KEY, tenantId);
        ctx.addZuulRequestHeader(Constants.USER_ID, tokenData.getStr(Constants.USER_ID_KEY));
        ctx.addZuulRequestHeader(Constants.AGENT_ID, tokenData.getStr(Constants.AGENT_ID_KEY));


        log.info("网关接收请求验证通过 {} URL {}", request.getMethod(), requestUri);
        log.debug("*****************AccessFilter run end*****************");
        return null;
    }

}
