package org.potato.security;

import org.potato.security.mvc.entity.AuthUser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityUser
 *
 * <p>
 *     SecurityUser 提供了常用的属性集，认证后通过 SecurityUtils.getSecurityInfo().getSecurityUser() 可以获取到安全用户信息
 * </p>
 *
 * <p>
 *     clientType: web,android,apple,wxapp,smartdoc
 * </p>
 */
public class SecurityUser extends AuthUser implements Serializable {

    private String accessToken;
    private String refreshToken;
    private String clientType;
    private List<String> roles;
    private List<String> perms;
    private Map<String, Object> extra;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPerms() {
        return perms;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}