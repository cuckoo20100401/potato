package org.potato.security.handler.impl;

import com.auth0.jwt.interfaces.Claim;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
import org.potato.security.TokenUtils;
import org.potato.security.handler.TokenHandler;
import org.potato.util.Result;
import org.potato.util.web.ResponseCode;

import java.util.Map;

/**
 * DefaultTokenHandler
 *
 * <p>
 *     项目配置时可参考这里的代码重写自定义处理器
 * </p>
 */
public class DefaultTokenHandler implements TokenHandler {

    @Override
    public String createToken(SecurityUser securityUser) {
        return TokenUtils.createToken(securityUser);
    }

    @Override
    public String createRefreshToken(SecurityUser securityUser) {
        return TokenUtils.createRefreshToken(securityUser);
    }

    @Override
    public SecurityInfo verifyAndParseToken(SecurityInfo securityInfo) {

        Result result = TokenUtils.verify(securityInfo.getSecurityUser().getAccessToken());

        if (result.isSuccess()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            SecurityUser securityUser = securityInfo.getSecurityUser();
            securityUser.setId(accessTokenClaims.get("id").asString());
            securityUser.setUsername(accessTokenClaims.get("username").asString());
            securityUser.setNickname(accessTokenClaims.get("nickname").asString());
            securityUser.setClientType(accessTokenClaims.get("clientType").asString());
            securityUser.setRoles(accessTokenClaims.get("roles").asList(String.class));
            securityUser.setPerms(accessTokenClaims.get("perms").asList(String.class));
            securityUser.setExtra(accessTokenClaims.get("extra").asMap());
            securityInfo.setValidateResult(Result.success());
        } else {
            if (result.code() == -1) {
                securityInfo.setValidateResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_EMPTY).message("token is empty"));
            } else if (result.code() == -2) {
                securityInfo.setValidateResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_EXPIRED).message("token is expired"));
            } else {
                securityInfo.setValidateResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_INVALID).message("token is invalid"));
            }
        }
        return securityInfo;
    }

    @Override
    public SecurityInfo verifyAndParseRefreshToken(SecurityInfo securityInfo) {

        Result result = TokenUtils.verify(securityInfo.getSecurityUser().getRefreshToken());

        if (result.isSuccess()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            SecurityUser securityUser = securityInfo.getSecurityUser();
            securityUser.setId(accessTokenClaims.get("id").asString());
            securityUser.setUsername(accessTokenClaims.get("username").asString());
            securityUser.setClientType(accessTokenClaims.get("clientType").asString());
            securityInfo.setValidateResult(Result.success());
        } else {
            if (result.code() == -1) {
                securityInfo.setValidateResult(Result.failure().code(-1).message("refreshToken is empty"));
            } else if (result.code() == -2) {
                securityInfo.setValidateResult(Result.failure().code(-2).message("refreshToken is expired"));
            } else {
                securityInfo.setValidateResult(Result.failure().code(-3).message("refreshToken is invalid"));
            }
        }
        return securityInfo;
    }
}