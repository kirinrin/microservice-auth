package me.kirinrin.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname CorePostFilter
 * @Description CORS前置过滤器
 * @Date 2020/3/16 10:02 下午
 * @Created by Kirinrin
 */
@Component
@Slf4j
public class CorePreFilter extends ZuulFilter {
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
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //只过滤OPTIONS 请求
        if(request.getMethod().equals(RequestMethod.OPTIONS.name())){
            log.debug("执行前置CORE过滤器");
            return true;
        }

        return false;
    }

    @Override
    public Object run() {
        log.debug("*****************FirstFilter run start*****************");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        HttpServletRequest request = ctx.getRequest();

        log.debug("Request Header = {}, value = {}", "Access-Control-Request-Headers", request.getHeader("Access-Control-Request-Headers"));



        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,DELETE,PATCH");
        response.setHeader("Access-Control-Expose-Headers","X-forwared-port, X-forwarded-host");
        response.setHeader("Vary","Origin,Access-Control-Request-Method,Access-Control-Request-Headers");
        //不再路由
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(200);
        log.debug("*****************FirstFilter run end*****************");
        return null;
    }
}
