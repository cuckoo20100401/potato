package org.potato.security.provider;

import org.potato.security.SecurityInfo;
import org.potato.security.config.SecurityConfiguration;

/**
 * AuthenticationProvider
 *
 * <p>
 *     该文件实现所有认证类型的通用方法，声明各认证类型中的差异化方法，让其具体的认证提供者子类去实现
 * </p>
 *
 * <p>
 *     注：在项目的配置文件中，必须指定具体的认证提供者
 * </p>
 */
public abstract class AuthenticationProvider {

    protected SecurityConfiguration securityConfiguration;

    /**
     * 在SecurityConfigurationBuilder.build()中装配上面的securityConfiguration变量
     * @param securityConfiguration
     */
    public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * 对当前请求进行安全检查，由于不同的认证方式实现逻辑不一样，所以此方法由具体的认证提供者去实现
     * @param securityInfo
     * @return
     */
    public abstract SecurityInfo check(SecurityInfo securityInfo);
}
