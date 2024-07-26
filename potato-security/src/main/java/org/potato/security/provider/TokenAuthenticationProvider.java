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
    public Authentication check(Authentication authentication) {

        //step obtain token from header
        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        AuthUser authUser = new AuthUser();
        authUser.setAccessToken(request.getHeader(Constants.TOKEN));
        authentication.setAuthUser(authUser);

        //step verify and parse token, or update token by method verifyToken. update token is optional
        authentication = securityConfiguration.getTokenHandler().verifyAndParseToken(authentication);
        if (authentication.getAuthResult().isSuccess()) {
            authentication.setAuthenticated(true);
            authentication.getRuntimeInstance().getLogInfo().put("check", "ok");
        } else {
            authentication.setAuthenticated(false);
            authentication.getRuntimeInstance().getLogInfo().put("check", authentication.getAuthResult().message());
        }
        return authentication;
    }
}