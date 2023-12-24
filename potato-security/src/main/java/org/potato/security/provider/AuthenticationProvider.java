package org.potato.security.provider;

import org.potato.security.AuthUtils;
import org.potato.security.Authentication;
import org.potato.security.config.SecurityConfiguration;
import org.potato.util.Result;

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
     * 校验authc，由于不同的认证方式实现逻辑不一样，所以此方法由具体的认证提供者去实现
     * @param authentication
     * @return
     */
    public abstract Authentication validateAuthc(Authentication authentication);

    /**
     * 校验角色和权限，目前分4种情况，通过正则表达式去匹配
     * @param authentication
     * @return
     */
    public Authentication validateRolesAndPerms(Authentication authentication) {

        String authRuleType = "auth-roles_and_perms";

        Result authResult = AuthUtils.validateAuthRuleForRolesAndPerms(authentication.getAuthRule(), authentication.getAuthUser().getRoles(), authentication.getAuthUser().getPerms());
        if (authResult.isNothing()) {
            authRuleType = "auth-roles_or_perms";
            authResult = AuthUtils.validateAuthRuleForRolesOrPerms(authentication.getAuthRule(), authentication.getAuthUser().getRoles(), authentication.getAuthUser().getPerms());
        }
        if (authResult.isNothing()) {
            authRuleType = "auth-roles";
            authResult = AuthUtils.validateAuthRuleForRoles(authentication.getAuthRule(), authentication.getAuthUser().getRoles(), authentication.getAuthUser().getPerms());
        }
        if (authResult.isNothing()) {
            authRuleType = "auth-perms";
            authResult = AuthUtils.validateAuthRuleForPerms(authentication.getAuthRule(), authentication.getAuthUser().getRoles(), authentication.getAuthUser().getPerms());
        }

        if (!authResult.isNothing()) {
            if (authResult.isSuccess()) {
                authentication.getRuntimeInstance().getLogInfo().put(authRuleType, "ok");
            } else {
                authentication.getRuntimeInstance().getLogInfo().put(authRuleType, authResult.message());
            }
            authentication.setAuthResult(authResult);
        } else {
            authentication.setAuthResult(Result.nothing().message("do nothing"));
        }
        return authentication;
    }
}