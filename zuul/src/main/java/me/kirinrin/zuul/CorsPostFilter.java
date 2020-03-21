package me.kirinrin.zuul;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

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
        return 4;
    }
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        printHeaderInfo(ctx, request);
        //过滤各种POST请求
        if(request.getMethod().equals(RequestMethod.OPTIONS.name()) && !request.getHeader(Origin).isEmpty()){
            log.debug("忽略后置CORE过滤器");
            return false;
        }
        log.debug("执行后置CORE过滤器");
        return true;
    }

    private void printHeaderInfo(RequestContext ctx, HttpServletRequest request) {
        HttpServletResponse response = ctx.getResponse();
        List<Pair<String, String>> headers = ctx.getZuulResponseHeaders();
        List<Pair<String, String>> originHeaders = ctx.getOriginResponseHeaders();
        String origin = request.getHeader(Origin);
        log.debug("Origin value = {}", origin);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String key = headerNames.nextElement();
            log.debug("request header {} - {}", key, request.getHeader(key));
        }
        Map<String, String> zuulRequestHeader = ctx.getZuulRequestHeaders();
        zuulRequestHeader.forEach((k, v)->log.debug("zuulRequestHeader k = {}, v = {}", k,v));
        response.getHeaderNames().forEach( n -> log.debug("response Header {}", n));
        log.debug("zuul header");
        headers.forEach((n) -> log.debug("zuul response header: {} - {}", n.first(), n.second()));
        log.debug("zuul origin header");
        originHeaders.forEach((n) -> log.debug("origin response header: {} - {}", n.first(), n.second()));
        log.debug("end all header....");
    }

    static final String Access_Control_Allow_Origin = "Access-Control-Allow-Origin";
    static final String Origin = "Origin";
    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        ctx.getOriginResponseHeaders();
        List<Pair<String, String>> originHeaders = ctx.getOriginResponseHeaders();
        for (Pair<String, String> header : originHeaders) {
            if (header.first().equalsIgnoreCase(Access_Control_Allow_Origin)){
                log.debug("下游服务返回了CORS头 Access_Control_Allow_Origin = {}", header.second());
                ctx.setSendZuulResponse(true);
                return null;
            }
        }
        log.debug("下游服务无CORS头, 添回");
        response.setHeader(Access_Control_Allow_Origin, "*");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Expose-Headers","X-forwared-port, X-forwarded-host");
        //允许继续路由
        ctx.setSendZuulResponse(true);

        return null;
    }
}
