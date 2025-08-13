package org.potato.security.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.TokenUtils;
import org.potato.security.handler.*;
import org.potato.security.handler.impl.*;
import org.potato.security.provider.AuthenticationProvider;

/**
 * SecurityConfigurationBuilder
 */
public class SecurityConfigurationBuilder {

    private static final Logger logger = LogManager.getLogger(SecurityConfiguration.class);

    private SecurityConfiguration securityConfiguration = new SecurityConfiguration();

    public SecurityConfigurationBuilder setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        securityConfiguration.setAuthenticationProvider(authenticationProvider);
        return this;
    }

    public SecurityConfigurationBuilder setCreateTokenSecret(String secret) {
        TokenUtils.secret = secret;
        return this;
    }
    public SecurityConfigurationBuilder setCreateTokenExpiredMinutes(long minutes) {
        TokenUtils.token_expired_minutes = minutes;
        return this;
    }

    public SecurityConfigurationBuilder enableGlobalSecurity(boolean enableGlobalSecurity) {
        securityConfiguration.setEnableGlobalSecurity(enableGlobalSecurity);
        return this;
    }

    public SecurityConfigurationBuilder enableRefreshToken(boolean enableRefreshToken) {
        securityConfiguration.setEnableRefreshToken(enableRefreshToken);
        return this;
    }

    public SecurityConfigurationBuilder addTokenHandler(TokenHandler tokenHandler) {
        securityConfiguration.setTokenHandler(tokenHandler);
        return this;
    }
    public SecurityConfigurationBuilder addLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        securityConfiguration.setLoginSuccessHandler(loginSuccessHandler);
        return this;
    }
    public SecurityConfigurationBuilder addValidationSuccessHandler(ValidationSuccessHandler validationSuccessHandler) {
        securityConfiguration.setValidationSuccessHandler(validationSuccessHandler);
        return this;
    }
    public SecurityConfigurationBuilder addValidationFailureHandler(ValidationFailureHandler validationFailureHandler) {
        securityConfiguration.setValidationFailureHandler(validationFailureHandler);
        return this;
    }
    public SecurityConfigurationBuilder addLogHandler(LogHandler logHandler) {
        securityConfiguration.setLogHandler(logHandler);
        return this;
    }

    /**
     * 当调用该方法时可以校验和补全默认的配置信息
     * @return
     */
    public SecurityConfiguration build() {

        // 当项目未配置时，默认不启用全局安全
        if (securityConfiguration.getEnableGlobalSecurity() == null) {
            securityConfiguration.setEnableGlobalSecurity(false);
        }

        // 当项目未配置时，默认不启用RefreshToken
        if (securityConfiguration.getEnableRefreshToken() == null) {
            securityConfiguration.setEnableRefreshToken(false);
        }

        // 当项目未配置自定义的Handler时，框架会使用默认的Handler实现类。每个Handler都提供了一个默认实现类
        if (securityConfiguration.getTokenHandler() == null) {
            securityConfiguration.setTokenHandler(new DefaultTokenHandler());
        }
        if (securityConfiguration.getLoginSuccessHandler() == null) {
            securityConfiguration.setLoginSuccessHandler(new DefaultLoginSuccessHandler());
        }
        if (securityConfiguration.getValidationSuccessHandler() == null) {
            securityConfiguration.setValidationSuccessHandler(new DefaultValidationSuccessHandler());
        }
        if (securityConfiguration.getValidationFailureHandler() == null) {
            securityConfiguration.setValidationFailureHandler(new DefaultValidationFailureHandler());
        }
        if (securityConfiguration.getLogHandler() == null) {
            securityConfiguration.setLogHandler(new DefaultLogHandler());
        }

        // AuthenticationProvider中需要获取SecurityConfiguration中的配置信息，故需要此设置，其它类需要配置信息时也是如此
        securityConfiguration.getAuthenticationProvider().setSecurityConfiguration(securityConfiguration);

        logger.info("build completed, enableGlobalSecurity is {}", securityConfiguration.getEnableGlobalSecurity());
        return securityConfiguration;
    }
}