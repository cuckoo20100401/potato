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

        Result result = TokenUtils.verify(securityInfo.getAuthUser().getAccessToken());

        if (result.isSuccess()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            SecurityUser securityUser = securityInfo.getAuthUser();
            securityUser.setId(accessTokenClaims.get("user.id") == null ? null : accessTokenClaims.get("user.id").asString());
            securityUser.setUsername(accessTokenClaims.get("user.username") == null ? null : accessTokenClaims.get("user.username").asString());
            securityUser.setNickname(accessTokenClaims.get("user.nickname") == null ? null : accessTokenClaims.get("user.nickname").asString());
            securityUser.setCellphone(accessTokenClaims.get("user.cellphone") == null ? null : accessTokenClaims.get("user.cellphone").asString());
            securityUser.setTelephone(accessTokenClaims.get("user.telephone") == null ? null : accessTokenClaims.get("user.telephone").asString());
            securityUser.setEmail(accessTokenClaims.get("user.email") == null ? null : accessTokenClaims.get("user.email").asString());
            securityUser.setCompanyId(accessTokenClaims.get("user.companyId") == null ? null : accessTokenClaims.get("user.companyId").asString());
            securityUser.setManageGroupIds(accessTokenClaims.get("user.manageGroupIds") == null ? null : accessTokenClaims.get("user.departmentId").asArray(String.class));
            securityUser.setClientType(accessTokenClaims.get("user.clientType") == null ? null : accessTokenClaims.get("user.clientType").asString());
            securityUser.setRoles(accessTokenClaims.get("user.roles").asArray(String.class));
            securityUser.setPerms(accessTokenClaims.get("user.perms").asArray(String.class));
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

        Result result = TokenUtils.verify(securityInfo.getAuthUser().getRefreshToken());

        if (result.isSuccess()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            SecurityUser securityUser = securityInfo.getAuthUser();
            securityUser.setId(accessTokenClaims.get("user.id") == null ? null : accessTokenClaims.get("user.id").asString());
            securityUser.setUsername(accessTokenClaims.get("user.username") == null ? null : accessTokenClaims.get("user.username").asString());
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