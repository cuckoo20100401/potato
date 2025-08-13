package org.potato.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
import org.potato.security.Constants;

/**
 * TokenAuthenticationProvider
 *
 * <p>
 *     此处仅需要实现非通用的方法，通用的方法由抽象类（即父类）去实现
 * </p>
 */
public class TokenAuthenticationProvider extends AuthenticationProvider {

    @Override
    public SecurityInfo check(SecurityInfo securityInfo) {

        // Obtain token from header
        HttpServletRequest request = (HttpServletRequest) securityInfo.getRuntimeInstance().getServletRequest();
        SecurityUser securityUser = new SecurityUser();
        securityUser.setAccessToken(request.getHeader(Constants.TOKEN));
        securityInfo.setSecurityUser(securityUser);

        // Verify and parse token
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