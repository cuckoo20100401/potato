package org.potato.security.handler;

import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;

public interface TokenHandler {

    /**
     * 创建Token
     *
     * @param securityUser
     * @return
     */
    String createToken(SecurityUser securityUser);

    /**
     * 创建refresh_token
     *
     * @param securityUser
     * @return
     */
    String createRefreshToken(SecurityUser securityUser);

    /**
     * 校验和解析token
     *
     * <p>
     *     在cookie+token的认证方式中，每次请求后可以更新token，以实现session的过期效果，但更新token是可选的、不是必须的，由项目决定是否更新
     * </p>
     * @param securityInfo
     * @return
     */
    SecurityInfo verifyAndParseToken(SecurityInfo securityInfo);

    /**
     * 校验和解析refresh_token
     *
     * @param securityInfo
     * @return
     */
    SecurityInfo verifyAndParseRefreshToken(SecurityInfo securityInfo);
}