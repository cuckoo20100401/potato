package org.potato.security.handler.impl;

import org.potato.security.SecurityUser;
import org.potato.security.handler.LoginSuccessHandler;

import java.util.Map;

/**
 * DefaultLoginSuccessHandler
 */
public class DefaultLoginSuccessHandler implements LoginSuccessHandler {

    /**
     * 用来添加在登录成功后给客户端返回的消息负载
     *
     * @param securityUser  数据库中的认证用户
     * @param authUserX 返回给客户端的消息负载
     */
    @Override
    public void onLoginSuccess(SecurityUser securityUser, Map<String, Object> authUserX) {

    }
}
