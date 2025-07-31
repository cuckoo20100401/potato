package org.potato.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import org.potato.security.AuthUser;
import org.potato.security.Authentication;
import org.potato.security.Constants;
import org.potato.util.Result;
import org.potato.util.web.ResponseCode;

/**
 * CookieAndSessionAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 */
public class CookieAndSessionAuthenticationProvider extends AuthenticationProvider {

    /**
     * check
     *
     * <p>
     *     没登录时和token不存在用的是同一个状态码
     * </p>
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication check(Authentication authentication) {

        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        if (request.getSession().getAttribute(Constants.AUTH_USER) != null) {
            authentication.setAuthenticated(true);
            authentication.getRuntimeInstance().getLogInfo().put("check", "ok");
            authentication.setAuthUser((AuthUser)request.getSession().getAttribute(Constants.AUTH_USER));
            authentication.setAuthResult(Result.success());
        } else {
            authentication.setAuthenticated(false);
            authentication.getRuntimeInstance().getLogInfo().put("check", "you have not been login");
            authentication.setAuthResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_EMPTY).message("you have not been login"));
        }
        return authentication;
    }
}