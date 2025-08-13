package org.potato.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * SecurityContextHolder
 */
public class SecurityUtils {

    /**
     * getAuthentication
     *
     * <p>
     *     无论是哪种认证方式，在check()方法中都会将authUser设置到Authentication对象中，然后把Authentication对象返回给AuthenticationManager，供AuthenticationManager后续校验角色和权限使用：
     *     1、在token认证方式中，check()是通过解析accessToken获取authUser信息来构造AuthUser对象的
     *     2、在session认证方式中，check()是通过获取session中保存的authUser来构造AuthUser对象的
     *     注：另外在AuthenticationManager中，无论是校验登录状态、角色或权限，只要校验通过后都会将Authentication对象保存到request中，以便后续代码中使用。这也是此处能获取到Authentication对象的原因。
     * </p>
     *
     * @return
     */
    public static SecurityInfo getSecurityInfo() {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return (SecurityInfo) request.getAttribute(Constants.SECURITY_INFO);
    }
}