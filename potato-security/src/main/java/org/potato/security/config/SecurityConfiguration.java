package org.potato.security.config;

import org.potato.security.handler.*;
import org.potato.security.provider.AuthenticationProvider;

/**
 * SecurityConfiguration
 */
public class SecurityConfiguration {

    private AuthenticationProvider authenticationProvider;
    private Boolean enableGlobalAuthenticated;
    private Boolean enableRefreshToken;
    private TokenHandler tokenHandler;
    private LoginSuccessHandler loginSuccessHandler;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private LogHandler logHandler;

    public static SecurityConfigurationBuilder builder() {
        return new SecurityConfigurationBuilder();
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public Boolean getEnableGlobalAuthenticated() {
        return enableGlobalAuthenticated;
    }

    public void setEnableGlobalAuthenticated(Boolean enableGlobalAuthenticated) {
        this.enableGlobalAuthenticated = enableGlobalAuthenticated;
    }

    public Boolean getEnableRefreshToken() {
        return enableRefreshToken;
    }

    public void setEnableRefreshToken(Boolean enableRefreshToken) {
        this.enableRefreshToken = enableRefreshToken;
    }

    public TokenHandler getTokenHandler() {
        return tokenHandler;
    }

    public void setTokenHandler(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    public LoginSuccessHandler getLoginSuccessHandler() {
        return loginSuccessHandler;
    }

    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public LogHandler getLogHandler() {
        return logHandler;
    }

    public void setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }
}