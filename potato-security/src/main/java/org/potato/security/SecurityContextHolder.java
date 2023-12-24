package org.potato.security;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * SecurityContextHolder
 */
public class SecurityContextHolder {

    /**
     * getAuthentication
     *
     * <p>
     *     无论是哪种认证方式，在validateAuthc()方法中都会将authUser设置到Authentication对象中，然后把Authentication对象返回给AuthenticationManager，供AuthenticationManager后续校验角色和权限使用：
     *     1、在token认证方式中，validateAuthc()是通过解析accessToken获取authUser信息来构造AuthUser对象的
     *     2、在session认证方式中，validateAuthc()是通过获取session中保存的authUser来构造AuthUser对象的
     *     注：另外在AuthenticationManager中，无论是校验登录状态、角色或权限，只要校验通过后都会将Authentication对象保存到request中，以便后续代码中使用。这也是此处能获取到Authentication对象的原因。
     * </p>
     *
     * @return
     */
    public static Authentication getAuthentication() {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return (Authentication) request.getAttribute(Constants.AUTHENTICATION);
    }
}