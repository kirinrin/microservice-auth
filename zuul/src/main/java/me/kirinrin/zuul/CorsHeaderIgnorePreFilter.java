package me.kirinrin.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname CorsHeaderIgnorePreFilter
 * @Description CORS前置过滤器, 过滤所有CORS头不向下传输
 * @Date 2020/3/16 10:02 下午
 * @Created by Kirinrin
 */
@Component
@Slf4j
public class CorsHeaderIgnorePreFilter extends ZuulFilter {
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
        return 3;
    }
    @Override
    public boolean shouldFilter() {
        return false;
    }

    static final String Access_Control_Allow_Origin = "Access-Control-Allow-Origin";

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        return null;
    }
}
