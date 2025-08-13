package org.potato.security.handler.impl;

import org.potato.security.SecurityInfo;
import org.potato.security.handler.ValidationFailureHandler;

/**
 * DefaultValidationFailureHandler
 *
 * <p>
 *     项目配置时可参考这里的代码重写自定义处理器
 * </p>
 */
public class DefaultValidationFailureHandler implements ValidationFailureHandler {

    @Override
    public Object onValidationFailure(SecurityInfo securityInfo) {
        return securityInfo.getValidateResult();
    }
}