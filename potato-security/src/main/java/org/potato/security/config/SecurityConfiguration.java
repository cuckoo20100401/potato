package org.potato.security.config;

import org.potato.security.handler.*;
import org.potato.security.provider.AuthenticationProvider;

/**
 * SecurityConfiguration
 */
public class SecurityConfiguration {

    private AuthenticationProvider authenticationProvider;
    private Boolean enableGlobalSecurity;
    private Boolean enableRefreshToken;
    private TokenHandler tokenHandler;
    private LoginSuccessHandler loginSuccessHandler;
    private ValidationSuccessHandler validationSuccessHandler;
    private ValidationFailureHandler validationFailureHandler;
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

    public Boolean getEnableGlobalSecurity() {
        return enableGlobalSecurity;
    }

    public void setEnableGlobalSecurity(Boolean enableGlobalSecurity) {
        this.enableGlobalSecurity = enableGlobalSecurity;
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

    public ValidationSuccessHandler getValidationSuccessHandler() {
        return validationSuccessHandler;
    }

    public void setValidationSuccessHandler(ValidationSuccessHandler validationSuccessHandler) {
        this.validationSuccessHandler = validationSuccessHandler;
    }

    public ValidationFailureHandler getValidationFailureHandler() {
        return validationFailureHandler;
    }

    public void setValidationFailureHandler(ValidationFailureHandler validationFailureHandler) {
        this.validationFailureHandler = validationFailureHandler;
    }

    public LogHandler getLogHandler() {
        return logHandler;
    }

    public void setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }
}