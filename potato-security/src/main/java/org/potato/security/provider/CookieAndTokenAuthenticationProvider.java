package org.potato.security.provider;

import org.potato.security.AuthUser;
import org.potato.security.Authentication;
import org.potato.security.Constants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * CookieAndTokenAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 *
 * <p>
 *     注：因为cookie的作用域只跟IP和路径有关，跟端口无关，所以当一台服务器部署多个项目时，可能会出现token被相互覆盖的情况（解决办法：一台服务器只有一个IP，但可以有不同的域名，按域名保存token即可）。
 * </p>
 */
public class CookieAndTokenAuthenticationProvider extends AuthenticationProvider {

    @Override
    public Authentication check(Authentication authentication) {

        //* obtain token from cookie
        String accessToken = null;
        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        if (request.getCookies() != null) {
            Cookie accessTokenCookie = Arrays.asList(request.getCookies()).stream().filter(cookie -> cookie.getName().equals(Constants.TOKEN)).findFirst().orElse(null);
            if (accessTokenCookie != null) {
                accessToken = accessTokenCookie.getValue();
            }
        }

        //* set token to Authentication.AuthUser
        AuthUser authUser = new AuthUser();
        authUser.setAccessToken(accessToken);
        authentication.setAuthUser(authUser);

        //* verify and parse token, or update token by method verifyToken. update token is optional
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