package org.potato.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
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
     * @param securityInfo
     * @return
     */
    @Override
    public SecurityInfo check(SecurityInfo securityInfo) {

        HttpServletRequest request = (HttpServletRequest) securityInfo.getRuntimeInstance().getServletRequest();
        if (request.getSession().getAttribute(Constants.SECURITY_USER) != null) {
            securityInfo.setAuthenticated(true);
            securityInfo.getRuntimeInstance().getLogInfo().put("check", "ok");
            securityInfo.setAuthUser((SecurityUser)request.getSession().getAttribute(Constants.SECURITY_USER));
            securityInfo.setValidateResult(Result.success());
        } else {
            securityInfo.setAuthenticated(false);
            securityInfo.getRuntimeInstance().getLogInfo().put("check", "you have not been login");
            securityInfo.setValidateResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_EMPTY).message("you have not been login"));
        }
        return securityInfo;
    }
}