package org.potato.security.handler;

import org.potato.security.SecurityInfo;

/**
 * LoginFailureHandler
 *
 * <p>
 *     指客户端登录时的处理器
 * </p>
 */
public interface LoginFailureHandler {

    void onLoginFailure(SecurityInfo securityInfo);
}