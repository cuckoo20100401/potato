package org.potato.security.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.potato.security.Authentication;
import org.potato.security.handler.AuthenticationFailureHandler;
import org.potato.util.Result;
import org.potato.util.web.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DefaultAuthenticationFailureHandler
 *
 * <p>
 *     项目配置时可参考这里的代码重写自定义处理器
 * </p>
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(Authentication authentication) {
        try {
            HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
            Result result = Result.failure().code(authentication.getAuthResult().code()).message(authentication.getAuthResult().message());
            ResponseUtils.writeJson(new ObjectMapper().writeValueAsString(result), response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}