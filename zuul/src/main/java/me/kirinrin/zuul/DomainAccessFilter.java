package me.kirinrin.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static me.kirinrin.zuul.RequestUtil.*;

/**
 * @Classname DomainAccessFilter
 * @Description 应用域访问权限前置过滤器, 用于区分各个App的访问域，不同域有各自的作用层.
 * xxx@xxx.cas-online.com 这样的账号只能使用业务系统，不能使用管理系统.
 * 1. 登录请求， 验证发送方，管理系统登录要求账号是格式xxx@cas-online.com，业务要求账号格式xxx@xxx.cas-online.com
 * 2. xxx@xxx.cas-online.com 不能使用 /cloud-management 下的请求。
 *
 * @Date 2020/4/3 10:02 下午
 * @author by Kirinrin
 */
@Component
@Slf4j
public class DomainAccessFilter extends ZuulFilter {
    final
    TokenAuthorityService service;

    public DomainAccessFilter(TokenAuthorityService service) {
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
        return 1;
    }
    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        //共享RequestContext，上下文对象
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        log.debug("判断请求是否执行域访问控制过滤器 URI = {}", request.getRequestURI());

        if (isLoginRequest(request.getRequestURI())) {
            return true;
        }
        if (isCloudManagementRequest(request.getRequestURI())){
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        log.debug("*****************DomainAccessFilter run start*****************");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        HttpServletRequest request = ctx.getRequest();

        log.debug("Request Header = {}, value = {}", "Access-Control-Request-Headers", request.getHeader("Access-Control-Request-Headers"));
        if (isLoginRequest(request.getRequestURI())) {
            log.debug("登录请求，验证请求原地址");
            // FIXME 需要讨论
        }
        if (isCloudManagementRequest(request.getRequestURI())){
            log.debug("云平台管理请求, 判断账号域");


        }


        log.debug("*****************DomainAccessFilter run end*****************");
        return null;
    }
}
