package org.potato.security.handler;

import org.potato.security.Authentication;

/**
 * LoginSuccessHandler
 *
 * <p>
 *     指客户端登录时的处理器
 * </p>
 */
public interface LoginSuccessHandler {

    void onLoginSuccess(Authentication authentication);
}