package org.potato.security.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.potato.security.Authentication;
import org.potato.security.Constants;
import org.potato.security.SecurityContextHolder;
import org.potato.security.annotation.RequiresPerms;
import org.potato.security.annotation.RequiresRoles;
import org.potato.security.annotation.Security;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.enumerate.Logical;
import org.potato.util.Result;
import org.potato.util.web.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * SecurityAspect
 */
@Aspect
@Component
public class SecurityAspect {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    /**
     * validateGlobalSecurity
     */
    @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object validateGlobalSecurity(ProceedingJoinPoint joinPoint) throws Throwable {

        if (!securityConfiguration.getEnableGlobalAuthenticated()) {
            return joinPoint.proceed();
        }

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (targetClass.isAnnotationPresent(Security.class) || targetMethod.isAnnotationPresent(Security.class)) {
            return joinPoint.proceed();
        }

        Authentication authentication = SecurityContextHolder.getAuthentication();
        if (authentication.isAuthenticated()) {
            authentication = this.setValidateResultAndPrintLog("validateGlobalSecurity", Result.success());
            securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
            return joinPoint.proceed();
        } else {
            authentication = this.setValidateResultAndPrintLog("validateGlobalSecurity", authentication.getAuthResult());
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }

    /**
     * validateSecurity
     */
    @Around("@within(org.potato.security.annotation.Security) || @annotation(org.potato.security.annotation.Security)")
    public Object validateSecurity(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (targetMethod.isAnnotationPresent(Security.class)) {
            return validateSecurityAnnotation(joinPoint, targetMethod.getAnnotation(Security.class));
        } else if (targetClass.isAnnotationPresent(Security.class)) {
            return validateSecurityAnnotation(joinPoint, targetClass.getAnnotation(Security.class));
        }
        return joinPoint.proceed();
    }

    private Object validateSecurityAnnotation(ProceedingJoinPoint joinPoint, Security securityAnnotation) throws Throwable {

        Authentication authentication = SecurityContextHolder.getAuthentication();

        //Validate Anonymous
        if (securityAnnotation.anonymous()) {
            this.setValidateResultAndPrintLog("validateAnonymous", Result.success());
            return joinPoint.proceed();
        }

        //Validate Authentication
        if (authentication.isAuthenticated()) {
            authentication = this.setValidateResult("validateAuthentication", Result.success());
        } else {
            authentication = this.setValidateResultAndPrintLog("validateAuthentication", authentication.getAuthResult());
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }

        //Validate Authorization:roles
        boolean validateRequiresRolesResult = false;
        RequiresRoles requiresRolesAnnotation = securityAnnotation.roles();
        if (requiresRolesAnnotation.value().length > 0) {
            int hasRoleCount = 0;
            for (String requiresRole: requiresRolesAnnotation.value()) {
                if (Arrays.stream(authentication.getAuthUser().getRoles()).toList().contains(requiresRole)) {
                    hasRoleCount++;
                }
            }
            if (requiresRolesAnnotation.logical().value().equals(Logical.AND.value())) {
                if (hasRoleCount == requiresRolesAnnotation.value().length) {
                    validateRequiresRolesResult = true;
                } else {
                    validateRequiresRolesResult = false;
                }
            }
            if (requiresRolesAnnotation.logical().value().equals(Logical.OR.value())) {
                if (hasRoleCount > 0) {
                    validateRequiresRolesResult = true;
                } else {
                    validateRequiresRolesResult = false;
                }
            }
            if (validateRequiresRolesResult) {
                authentication = this.setValidateResult("validateAuthorization[role-"+requiresRolesAnnotation.logical().value()+"-role]", Result.success());
            } else {
                authentication = this.setValidateResultAndPrintLog("validateAuthorization[role-"+requiresRolesAnnotation.logical().value()+"-role]", Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("no permission"));
                return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
            }
        }

        //Validate Authorization:perms
        boolean validateRequiresPermsResult = false;
        RequiresPerms requiresPermsAnnotation = securityAnnotation.perms();
        if (requiresPermsAnnotation.value().length > 0) {
            int hasPermCount = 0;
            for (String requiresPerm: requiresPermsAnnotation.value()) {
                if (Arrays.stream(authentication.getAuthUser().getPerms()).toList().contains(requiresPerm)) {
                    hasPermCount++;
                }
            }
            if (requiresPermsAnnotation.logical().value().equals(Logical.AND.value())) {
                if (hasPermCount == requiresPermsAnnotation.value().length) {
                    validateRequiresPermsResult = true;
                } else {
                    validateRequiresPermsResult = false;
                }
            }
            if (requiresPermsAnnotation.logical().value().equals(Logical.OR.value())) {
                if (hasPermCount > 0) {
                    validateRequiresPermsResult = true;
                } else {
                    validateRequiresPermsResult = false;
                }
            }
            if (validateRequiresPermsResult) {
                authentication = this.setValidateResult("validateAuthorization[perm-"+requiresPermsAnnotation.logical().value()+"-perm]", Result.success());
            } else {
                authentication = this.setValidateResultAndPrintLog("validateAuthorization[perm-"+requiresPermsAnnotation.logical().value()+"-perm]", Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("no permission"));
                return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
            }
        }

        //Validate Authorization:logical of roles and perms
        if (requiresRolesAnnotation.value().length > 0 && requiresPermsAnnotation.value().length > 0) {
            if (securityAnnotation.logical().value().equals(Logical.AND.value())) {
                if (validateRequiresRolesResult && validateRequiresPermsResult) {
                    authentication = this.setValidateResult("validateAuthorization[role-"+securityAnnotation.logical().value()+"-perm]", Result.success());
                } else {
                    authentication = this.setValidateResultAndPrintLog("validateAuthorization[role-"+securityAnnotation.logical().value()+"-perm]", Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("no permission"));
                    return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
                }
            }
            if (securityAnnotation.logical().value().equals(Logical.OR.value())) {
                if (validateRequiresRolesResult || validateRequiresPermsResult) {
                    authentication = this.setValidateResult("validateAuthorization[role-"+securityAnnotation.logical().value()+"-perm]", Result.success());
                } else {
                    authentication = this.setValidateResultAndPrintLog("validateAuthorization[role-"+securityAnnotation.logical().value()+"-perm]", Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("no permission"));
                    return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
                }
            }
        }

        //Passed and allow access resources
        this.printLog(authentication);
        securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
        return joinPoint.proceed();
    }

    private Authentication setValidateResult(String authLabel, Result authResult) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        authentication.setAuthResult(authResult);
        authentication.getRuntimeInstance().getLogInfo().put(authLabel, authResult.message());
        authentication.getRuntimeInstance().getServletRequest().setAttribute(Constants.AUTHENTICATION, authentication);
        return authentication;
    }
    private Authentication setValidateResultAndPrintLog(String authLabel, Result authResult) {
        Authentication authentication = this.setValidateResult(authLabel, authResult);
        this.printLog(authentication);
        return authentication;
    }
    private void printLog(Authentication authentication) {
        securityConfiguration.getLogHandler().onLog(authentication.getRuntimeInstance().getLogInfo());
    }
}
