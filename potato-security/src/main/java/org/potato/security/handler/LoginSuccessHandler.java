package org.potato.security.handler;

import org.potato.security.AuthUser;

import java.util.Map;

/**
 * LoginSuccessHandler
 *
 * <p>
 *     指客户端登录成功后的处理器
 * </p>
 */
public interface LoginSuccessHandler {

    /**
     * 用来添加在登录成功后给客户端返回的消息负载
     *
     * @param authUser   数据库中的认证用户
     * @param authUserX  返回给客户端的消息负载
     */
    void onLoginSuccess(AuthUser authUser, Map<String, Object> authUserX);
}