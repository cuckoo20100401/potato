package org.potato.security.handler.impl;

import com.auth0.jwt.interfaces.Claim;
import org.potato.security.AuthUser;
import org.potato.security.Authentication;
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
    public String createToken(AuthUser authUser) {
        return TokenUtils.createToken(authUser);
    }

    @Override
    public String createRefreshToken(AuthUser authUser) {
        return TokenUtils.createRefreshToken(authUser);
    }

    @Override
    public Authentication verifyAndParseToken(Authentication authentication) {

        Result result = TokenUtils.verify(authentication.getAuthUser().getAccessToken());

        if (result.isSuccess()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            AuthUser authUser = authentication.getAuthUser();
            authUser.setId(accessTokenClaims.get("user.id") == null ? null : accessTokenClaims.get("user.id").asString());
            authUser.setUsername(accessTokenClaims.get("user.username") == null ? null : accessTokenClaims.get("user.username").asString());
            authUser.setNickname(accessTokenClaims.get("user.nickname") == null ? null : accessTokenClaims.get("user.nickname").asString());
            authUser.setCellphone(accessTokenClaims.get("user.cellphone") == null ? null : accessTokenClaims.get("user.cellphone").asString());
            authUser.setTelephone(accessTokenClaims.get("user.telephone") == null ? null : accessTokenClaims.get("user.telephone").asString());
            authUser.setEmail(accessTokenClaims.get("user.email") == null ? null : accessTokenClaims.get("user.email").asString());
            authUser.setCompanyId(accessTokenClaims.get("user.companyId") == null ? null : accessTokenClaims.get("user.companyId").asString());
            authUser.setManageGroupIds(accessTokenClaims.get("user.manageGroupIds") == null ? null : accessTokenClaims.get("user.departmentId").asArray(String.class));
            authUser.setClientType(accessTokenClaims.get("user.clientType") == null ? null : accessTokenClaims.get("user.clientType").asString());
            authUser.setRoles(accessTokenClaims.get("user.roles").asArray(String.class));
            authUser.setPerms(accessTokenClaims.get("user.perms").asArray(String.class));
            authentication.setAuthResult(Result.success());
        } else {
            if (result.code() == -1) {
                authentication.setAuthResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_EMPTY).message("token is empty"));
            } else if (result.code() == -2) {
                authentication.setAuthResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_EXPIRED).message("token is expired"));
            } else {
                authentication.setAuthResult(Result.failure().code(ResponseCode.AUTH_TOKEN_IS_INVALID).message("token is invalid"));
            }
        }
        return authentication;
    }

    @Override
    public Authentication verifyAndParseRefreshToken(Authentication authentication) {

        Result result = TokenUtils.verify(authentication.getAuthUser().getRefreshToken());

        if (result.isSuccess()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            AuthUser authUser = authentication.getAuthUser();
            authUser.setId(accessTokenClaims.get("user.id") == null ? null : accessTokenClaims.get("user.id").asString());
            authUser.setUsername(accessTokenClaims.get("user.username") == null ? null : accessTokenClaims.get("user.username").asString());
            authentication.setAuthResult(Result.success());
        } else {
            if (result.code() == -1) {
                authentication.setAuthResult(Result.failure().code(-1).message("refreshToken is empty"));
            } else if (result.code() == -2) {
                authentication.setAuthResult(Result.failure().code(-2).message("refreshToken is expired"));
            } else {
                authentication.setAuthResult(Result.failure().code(-3).message("refreshToken is invalid"));
            }
        }
        return authentication;
    }
}