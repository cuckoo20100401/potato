package org.potato.security.handler.impl;

import org.potato.security.handler.LoginSuccessHandler;

import java.util.Map;

/**
 * DefaultLoginSuccessHandler
 */
public class DefaultLoginSuccessHandler implements LoginSuccessHandler {

    /**
     * 用来添加在登录成功后给客户端返回的额外属性
     *
     * @param authUserX 返回给客户端的消息负载
     */
    @Override
    public void onLoginSuccess(Map<String, Object> authUserX) {

    }
}
