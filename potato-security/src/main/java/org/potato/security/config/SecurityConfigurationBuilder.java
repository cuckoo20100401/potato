package org.potato.security.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.TokenUtils;
import org.potato.security.handler.AuthenticationFailureHandler;
import org.potato.security.handler.AuthenticationSuccessHandler;
import org.potato.security.handler.LoginSuccessHandler;
import org.potato.security.handler.TokenHandler;
import org.potato.security.handler.impl.DefaultAuthenticationFailureHandler;
import org.potato.security.handler.impl.DefaultAuthenticationSuccessHandler;
import org.potato.security.handler.impl.DefaultLoginSuccessHandler;
import org.potato.security.handler.impl.DefaultTokenHandler;
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

    public SecurityConfigurationBuilder enableGlobalAuthenticated(boolean enableGlobalAuthenticated) {
        securityConfiguration.setEnableGlobalAuthenticated(enableGlobalAuthenticated);
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
    public SecurityConfigurationBuilder addAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        securityConfiguration.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        return this;
    }
    public SecurityConfigurationBuilder addAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        securityConfiguration.setAuthenticationFailureHandler(authenticationFailureHandler);
        return this;
    }

    /**
     * 当调用该方法时可以校验和补全默认的配置信息
     * @return
     */
    public SecurityConfiguration build() {

        // 当项目未配置时，默认不启用全局认证
        if (securityConfiguration.getEnableGlobalAuthenticated() == null) {
            securityConfiguration.setEnableGlobalAuthenticated(false);
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
        if (securityConfiguration.getAuthenticationSuccessHandler() == null) {
            securityConfiguration.setAuthenticationSuccessHandler(new DefaultAuthenticationSuccessHandler());
        }
        if (securityConfiguration.getAuthenticationFailureHandler() == null) {
            securityConfiguration.setAuthenticationFailureHandler(new DefaultAuthenticationFailureHandler());
        }

        // AuthenticationProvider中需要获取SecurityConfiguration中的配置信息，故需要此设置，其它类需要配置信息时也是如此
        securityConfiguration.getAuthenticationProvider().setSecurityConfiguration(securityConfiguration);

        logger.info("build completed, enableGlobalAuthenticated is {}", securityConfiguration.getEnableGlobalAuthenticated());
        return securityConfiguration;
    }
}