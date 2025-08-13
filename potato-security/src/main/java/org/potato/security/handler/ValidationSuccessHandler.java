package org.potato.security.handler;

import org.potato.security.SecurityInfo;

/**
 * ValidationSuccessHandler
 *
 * <p>
 *     指客户端登录成功后每次请求时的认证处理器
 * </p>
 */
public interface ValidationSuccessHandler {

    /**
     * 在认证成功时执行，由项目实现
     *
     * <p>
     *     在该方法中，可以在请求到达后端业务程序之前执行某种操作。因为参数authentication中的运行时属性非常丰富，比如包含request、authUser等，示例如下：
     *          HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
     *          request.setAttribute("name", "Lily");
     * </p>
     *
     * <p>
     *     注：只要当前请求通过认证过滤器到达后端业务程序即会执行，有如下几种情况：
     *     1、当认证规则authRules没配置为空时，直接通过
     *     2、当没有匹配的认证模板matchedAuthPattern时，直接通过
     *     3、当有匹配的认证模板matchedAuthPattern但没有匹配的认证规则matchedAuthRule时，直接通过
     *     4、当匹配的认证规则matchedAuthRule为匿名anon时，直接通过
     *     5、当匹配的认证规则matchedAuthRule仅为authc、并校验通过时
     *     6、当匹配的认证规则matchedAuthRule为authc和角色权限校验、并校验通过时
     * </p>
     * @param securityInfo
     */
    void onValidationSuccess(SecurityInfo securityInfo);
}