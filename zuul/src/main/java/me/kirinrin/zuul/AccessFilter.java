package me.kirinrin.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kirinrin
 */
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    static final String TOKEN_KEY = "access_token";
    static final String LOGIN_ACTION_URI = "/author/login";
    static final String RES_COMPANY_KEY = "tenant_id";

    @Autowired
    TokenAuthorityService service;

    @Override
    public String filterType() {
        //前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        //共享RequestContext，上下文对象
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        //不需要token校验的URL
        if (LOGIN_ACTION_URI.equalsIgnoreCase(request.getRequestURI())) {
            //登录接口不需要校验
            return false;
        }
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();

        log.info("网关接收请求 {} URL {}", request.getMethod(), requestURI);

        //获取传来的参数accessToken
        String tokenString = request.getHeader(TOKEN_KEY) != null ? request.getHeader(TOKEN_KEY) : request.getParameter(TOKEN_KEY);

        if (tokenString == null) {
            log.warn("access token is empty!");
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{\"result\":\"accessToken is empty!\"}");
            return null;
        }

        String tokenData = service.getTokenData(tokenString);
        log.debug("token = {} tokenData = {}", tokenString, tokenData);
        if (tokenData == null) {
            log.warn("access token is invalid!");
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            ctx.setResponseBody("{\"result\":\"accessToken is invalid!\"}");
            return null;
        }

        if(!validateUriAuthority(tokenData, requestURI)){
            log.warn("access token is invalid can access URI {}", requestURI);
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            ctx.setResponseBody("{\"result\":\"Permission denied\"}");
            return null;
        }

        ctx.addZuulRequestHeader(RES_COMPANY_KEY,tokenData);

        refreshToken(tokenString);

        log.info("网关接收请求验证通过 {} URL {}", request.getMethod(), requestURI);
        return null;
    }

    private void refreshToken(String tokenString) {
        log.debug("网关刷新token 延长过期时间 token = ", tokenString);
        //TODO 刷新 token
    }

    private boolean validateUriAuthority(String tokenData, String uri){
        log.debug("网关检查是否有权限访问该接口");
        //TODO 验证是否具有权限
        return true;
    }
}
