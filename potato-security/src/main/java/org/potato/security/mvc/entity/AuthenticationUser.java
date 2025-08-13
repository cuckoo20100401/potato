package org.potato.security.mvc.entity;

public class AuthenticationUser {

    private String username;
    private String password;

    /**
     * web,android,apple,wxapp,smartdoc|smartdoc
     */
    private String clientType;

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

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
