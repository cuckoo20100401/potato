package org.potato.security.handler;

import org.potato.security.SecurityInfo;

/**
 * ValidationFailureHandler
 *
 * <p>
 *     指客户端登录成功后每次请求时的认证处理器
 * </p>
 */
public interface ValidationFailureHandler {

    /**
     * 在认证失败时执行，由项目实现
     *
     * <p>
     *     在该方法中，可以向客户端发送json等消息或跳转页面
     *     1、发送json消息示例：
     *          HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
     *          PrintWriter printWriter = response.getWriter();
     *          response.setContentType("application/json;charset=utf-8");
     *          printWriter.write("auth failure");
     *          printWriter.flush();
     *     2、跳转页面示例：
     *          HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
     *          response.sendRedirect("/error.html");
     * </p>
     *
     * <p>
     *     注：若当前请求校验失败不能通过认证过滤器到达不了后端业务程序时即会执行，有如下几种情况：
     *     1、当匹配的认证规则matchedAuthRule仅为authc、并校验失败时
     *     2、当匹配的认证规则matchedAuthRule为authc和角色权限校验、并校验失败时
     * </p>
     * @param securityInfo
     */
    Object onValidationFailure(SecurityInfo securityInfo);
}