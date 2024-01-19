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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * SecurityConfigurationBuilder
 */
public class SecurityConfigurationBuilder {

    private static final Logger logger = LogManager.getLogger(SecurityConfiguration.class);

    private SecurityConfiguration securityConfiguration = new SecurityConfiguration();

    public SecurityConfigurationBuilder() {
        //* 初始化认证规则
        securityConfiguration.setAuthRules(new LinkedHashMap<>());
        securityConfiguration.getAuthRules().put("/auth/login", "anon");
        securityConfiguration.getAuthRules().put("/auth/refreshToken", "anon");
    }

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

    public SecurityConfigurationBuilder addAuthenticationRule(String authPattern, String authRule) {
        securityConfiguration.getAuthRules().put(authPattern, authRule);
        return this;
    }
    public SecurityConfigurationBuilder addAuthenticationRuleFromProperties(String filename) {
        try {
            Resource resource = new ClassPathResource(filename);
            if (!resource.exists()) {
                logger.error("The configuration file ("+filename+") does not exist");
                return this;
            }
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            properties.keySet().forEach(key -> {
                securityConfiguration.getAuthRules().put(key.toString(), properties.getProperty(key.toString()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * 在此方法中，可以将SecurityConfiguration对象装配给框架中所有需要用到框架配置信息的类文件中，比如AuthenticationManager、AuthenticationProvider都需要用到框架配
     * 置信息，但只有项目配置文件中设置的bean才需要用此方式装配，框架内部的类直接通过@Autowired注解装配即可
     * @return
     */
    public SecurityConfiguration build() {

        //* 当项目未配置自定义的Handler时，框架会使用默认的Handler实现类。每个Handler都提供了一个默认实现类
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

        //* AuthenticationProvider中需要调用SecurityConfiguration中的配置信息，故需要此设置，其它类需要配置信息时也是如此
        securityConfiguration.getAuthenticationProvider().setSecurityConfiguration(securityConfiguration);
        return securityConfiguration;
    }
}