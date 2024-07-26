package org.potato.security.handler.impl;

import org.potato.security.Authentication;
import org.potato.security.handler.AuthenticationFailureHandler;
import org.potato.util.Result;

/**
 * DefaultAuthenticationFailureHandler
 *
 * <p>
 *     项目配置时可参考这里的代码重写自定义处理器
 * </p>
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public Object onAuthenticationFailure(Authentication authentication) {
        return Result.failure().code(authentication.getAuthResult().code()).message(authentication.getAuthResult().message());
    }
}