package me.kirinrin.zuul;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Objects;

/**
 * @author by Kirinrin
 * @Classname RequestUtil
 * @Description 用于处理URL URI比较的常用方法
 * @Date 2020/4/3 10:53 上午
 */
public class RequestUtil {
    /**
     * /urule 因为设计器，使用嵌套 iFrame vue的方式，内层的 vue 部分无法添加请求头，决定针对所有这个路径的请求都放行。
     * /wordcloud只用于测试和开发
     *
     * @param uri
     * @return
     */
    public static boolean is3partRequest(String uri) {
        if(uri == null){ return false;}
        if (uri.startsWith(Constants.URULE_URI)) {
            return true;
        } else if (uri.startsWith(Constants.WORDCLOUD_URI)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param uri
     * @return
     */
    public static boolean isStaticResource(String uri) {
        for (String res : Constants.STATIC_RESOURCE) {
            if (uri.endsWith(res) || uri.endsWith(res.toUpperCase())) {
                return true;
            }
        }
        if (uri.startsWith(Constants.REPORTER_URI)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param uri
     * @return
     */
    public static boolean isLoginLogoutRequest(String uri){
        if (Constants.LOGIN_ACTION_URI.equalsIgnoreCase(uri) || Constants.LOGOUT_ACTION_URI.equalsIgnoreCase(uri)) {
            //登录接口不需要校验
            return true;
        }
        return false;
    }
    /**
     *
     * @param uri
     * @return
     */
    public static boolean isLoginRequest(String uri){
        if (Constants.LOGIN_ACTION_URI.equalsIgnoreCase(uri)) {
            //登录接口不需要校验
            return true;
        }
        return false;
    }

    /**
     *
     * @param method
     * @return
     */
    public static boolean isOptionRequest(String method){
        return Objects.equals(RequestMethod.OPTIONS.name(), method);
    }



    public static boolean isCloudManagementRequest(String requestUri) {
        return requestUri.startsWith(Constants.CLOUD_MANAGEMENT_URI);
    }


    public static boolean isChildDomainAccess(String domain, String childDomain) {
        if (domain != null && childDomain != null && childDomain.endsWith(domain)){
            return true;
        }else {
            return false;
        }
    }
}
