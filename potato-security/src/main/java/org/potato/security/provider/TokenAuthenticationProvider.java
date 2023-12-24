package org.potato.security.provider;

import org.potato.security.AuthUser;
import org.potato.security.Authentication;
import org.potato.security.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * TokenAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 */
public class TokenAuthenticationProvider extends AuthenticationProvider {

    @Override
    public Authentication validateAuthc(Authentication authentication) {

        //* obtain token from header
        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        AuthUser authUser = new AuthUser();
        authUser.setAccessToken(request.getHeader(Constants.TOKEN));
        authentication.setAuthUser(authUser);

        //* verify and parse token, or update token by method verifyToken. update token is optional
        authentication = securityConfiguration.getTokenHandler().verifyAndParseToken(authentication);
        if (authentication.getAuthResult().isSuccess()) {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", "ok");
        } else {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", authentication.getAuthResult().message());
        }
        return authentication;
    }
}