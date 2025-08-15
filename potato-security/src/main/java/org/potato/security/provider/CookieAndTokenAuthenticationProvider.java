package org.potato.security.provider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.potato.security.SecurityInfo;
import org.potato.security.Constants;

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
    public SecurityInfo check(SecurityInfo securityInfo) {

        // Obtain token from cookie
        String accessToken = null;
        HttpServletRequest request = (HttpServletRequest) securityInfo.getRuntimeInstance().getServletRequest();
        if (request.getCookies() != null) {
            Cookie accessTokenCookie = Arrays.asList(request.getCookies()).stream().filter(cookie -> cookie.getName().equals(Constants.TOKEN)).findFirst().orElse(null);
            if (accessTokenCookie != null) {
                accessToken = accessTokenCookie.getValue();
            }
        }
        securityInfo.getSecurityUser().setAccessToken(accessToken);

        // Verify and parse token. And can update token in here, update token is optional
        securityInfo = securityConfiguration.getTokenHandler().verifyAndParseToken(securityInfo);
        if (securityInfo.getValidateResult().isSuccess()) {
            securityInfo.setAuthenticated(true);
            securityInfo.getRuntimeInstance().getLogInfo().put("check", "ok");
        } else {
            securityInfo.setAuthenticated(false);
            securityInfo.getRuntimeInstance().getLogInfo().put("check", securityInfo.getValidateResult().message());
        }
        return securityInfo;
    }
}