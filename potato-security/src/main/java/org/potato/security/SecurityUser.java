package org.potato.security;

import java.io.Serializable;

/**
 * SecurityUser
 *
 * <p>
 *     class SysUser extends SecurityUser
 * </p>
 *
 * <p>
 *     SecurityUser 提供了常用的属性集，认证后通过 SecurityUtils 可以获取到安全用户信息
 * </p>
 */
public class SecurityUser implements Serializable {

    private String id;
    private String accessToken;
    private String refreshToken;
    private String username;
    private String password;
    private String nickname;
    private String cellphone;
    private String telephone;
    private String email;
    private String companyId;
    private String[] manageGroupIds;
    private String clientType;
    private Integer status;
    private String[] roles = new String[]{};
    private String[] perms = new String[]{};

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String[] getManageGroupIds() {
        return manageGroupIds;
    }

    public void setManageGroupIds(String[] manageGroupIds) {
        this.manageGroupIds = manageGroupIds;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getPerms() {
        return perms;
    }

    public void setPerms(String[] perms) {
        this.perms = perms;
    }
}