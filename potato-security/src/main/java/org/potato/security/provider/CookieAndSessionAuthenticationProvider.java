package org.potato.security.provider;

import org.potato.security.AuthUser;
import org.potato.security.Authentication;
import org.potato.security.Constants;
import org.potato.util.Result;
import org.potato.util.web.ResponseCode;

import javax.servlet.http.HttpServletRequest;

/**
 * CookieAndSessionAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 */
public class CookieAndSessionAuthenticationProvider extends AuthenticationProvider {

    /**
     * validateAuthc
     *
     * <p>
     *     没登录时和token不存在用的是同一个状态码
     * </p>
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication validateAuthc(Authentication authentication) {

        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        if (request.getSession().getAttribute(Constants.AUTH_USER) != null) {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", "ok");
            authentication.setAuthUser((AuthUser)request.getSession().getAttribute(Constants.AUTH_USER));
            authentication.setAuthResult(Result.success());
        } else {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", "you have not been login");
            authentication.setAuthResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_REQUIRED).message("Sorry, you have not been login"));
        }
        return authentication;
    }
}