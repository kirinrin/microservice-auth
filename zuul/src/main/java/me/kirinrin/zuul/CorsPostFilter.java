package me.kirinrin.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
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
public class CorsPostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        /*
        pre：可以在请求被路由之前调用
        route：在路由请求时候被调用
        post：在route和error过滤器之后被调用
        error：处理请求时发生错误时被调用
        * */
        // 前置过滤器
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        //// 优先级为0，数字越大，优先级越低
        return 1;
    }
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //过滤各种POST请求
        if(request.getMethod().equals(RequestMethod.OPTIONS.name()) && !request.getHeader("Origin").isEmpty()){
            return false;
        }
        log.debug("执行后置CORE过滤器");
        return true;
    }

    static final String Access_Control_Allow_Origin = "Access-Control-Allow-Origin";

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        HttpServletRequest request = ctx.getRequest();
        log.debug("后置过滤器 Access-Control-Allow-Origin = {}", response.getHeader(Access_Control_Allow_Origin));

        response.setHeader(Access_Control_Allow_Origin, "*");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Expose-Headers","X-forwared-port, X-forwarded-host");
        //允许继续路由
        ctx.setSendZuulResponse(true);
//        ctx.setResponseStatusCode(200);
        log.debug("后置过滤器 Access-Control-Allow-Origin = {}", response.getHeader(Access_Control_Allow_Origin));
        log.debug("*****************PostFilter run end*****************");
        return null;
    }
}
