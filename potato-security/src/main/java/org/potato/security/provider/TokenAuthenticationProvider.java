package org.potato.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
import org.potato.security.Constants;

/**
 * TokenAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 */
public class TokenAuthenticationProvider extends AuthenticationProvider {

    @Override
    public SecurityInfo check(SecurityInfo securityInfo) {

        //step obtain token from header
        HttpServletRequest request = (HttpServletRequest) securityInfo.getRuntimeInstance().getServletRequest();
        SecurityUser securityUser = new SecurityUser();
        securityUser.setAccessToken(request.getHeader(Constants.TOKEN));
        securityInfo.setSecurityUser(securityUser);

        //step verify and parse token, or update token by method verifyToken. update token is optional
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