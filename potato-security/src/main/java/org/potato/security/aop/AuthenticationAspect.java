package org.potato.security.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.potato.security.Authentication;
import org.potato.security.Constants;
import org.potato.security.SecurityContextHolder;
import org.potato.security.annotation.*;
import org.potato.security.config.SecurityConfiguration;
import org.potato.util.Result;
import org.potato.util.web.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AuthenticationAspect
 *
 * <p>
 *     同 AuthenticationManager 一起，相当于 Apache Shiro 中的 SecurityManager
 * </p>
 */
@Aspect
@Component
public class AuthenticationAspect {

    private static final Logger logger = LogManager.getLogger(AuthenticationAspect.class);
    private static final Class[] annotations = {
            Anonymous.class,
            Authenticated.class,
            RequiresPerm.class,
            RequiresRole.class,
            RequiresRoleAndPerm.class,
            RequiresRoleOrPerm.class
    };

    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Anonymous
     */
    @Around("@within(org.potato.security.annotation.Anonymous) || @annotation(org.potato.security.annotation.Anonymous)")
    public Object anonymous(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = this.setAuthResultAndPrintLog("check-@Anonymous", Result.success().code(), Result.success().message());
        return joinPoint.proceed();
    }

    /**
     * Authenticated
     */
    @Around("@within(org.potato.security.annotation.Authenticated) || @annotation(org.potato.security.annotation.Authenticated)")
    public Object authenticated(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getAuthentication();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        for (Class annotationClass: annotations) {
            if (annotationClass == Authenticated.class) {
                continue;
            }
            if (targetClass.isAnnotationPresent(annotationClass)) {
                return joinPoint.proceed();
            }
            if (targetMethod.isAnnotationPresent(annotationClass)) {
                return joinPoint.proceed();
            }
        }

        if (authentication.isAuthenticated()) {
            authentication = this.setAuthResultAndPrintLog("check-@Authenticated", Result.success().code(), Result.success().message());
            securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
            return joinPoint.proceed();
        } else {
            authentication = this.setAuthResultAndPrintLog("check-@Authenticated", authentication);
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }

    /**
     * GlobalAuthenticated
     */
    @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object globalAuthenticated(ProceedingJoinPoint joinPoint) throws Throwable {

        if (!securityConfiguration.getEnableGlobalAuthenticated()) {
            return joinPoint.proceed();
        }

        Authentication authentication = SecurityContextHolder.getAuthentication();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        for (Class annotationClass: annotations) {
            if (targetClass.isAnnotationPresent(annotationClass)) {
                return joinPoint.proceed();
            }
            if (targetMethod.isAnnotationPresent(annotationClass)) {
                return joinPoint.proceed();
            }
        }

        if (authentication.isAuthenticated()) {
            authentication = this.setAuthResultAndPrintLog("check-@GlobalAuthenticated", Result.success().code(), Result.success().message());
            securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
            return joinPoint.proceed();
        } else {
            authentication = this.setAuthResultAndPrintLog("check-@GlobalAuthenticated", authentication);
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }

    /**
     * RequiresPerm
     */
    @Around("@within(org.potato.security.annotation.RequiresPerm) || @annotation(org.potato.security.annotation.RequiresPerm)")
    public Object requiresPerm(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getAuthentication();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (targetClass.isAnnotationPresent(Anonymous.class) || targetMethod.isAnnotationPresent(Anonymous.class)) {
            return joinPoint.proceed();
        }

        if (authentication.isAuthenticated()) {
            RequiresPerm requiresPerm = targetMethod.isAnnotationPresent(RequiresPerm.class) ? targetMethod.getAnnotation(RequiresPerm.class) : targetClass.getAnnotation(RequiresPerm.class);
            if (Arrays.asList(authentication.getAuthUser().getPerms()).contains(requiresPerm.value())) {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresPerm", Result.success().code(), Result.success().message());
                securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
                return joinPoint.proceed();
            } else {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresPerm", ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION, "no permission");
                return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
            }
        } else {
            authentication = this.setAuthResultAndPrintLog("check-@Authenticated", authentication);
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }

    /**
     * RequiresRole
     */
    @Around("@within(org.potato.security.annotation.RequiresRole) || @annotation(org.potato.security.annotation.RequiresRole)")
    public Object requiresRole(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getAuthentication();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (targetClass.isAnnotationPresent(Anonymous.class) || targetMethod.isAnnotationPresent(Anonymous.class)) {
            return joinPoint.proceed();
        }

        if (authentication.isAuthenticated()) {
            RequiresRole requiresRole = targetMethod.isAnnotationPresent(RequiresRole.class) ? targetMethod.getAnnotation(RequiresRole.class) : targetClass.getAnnotation(RequiresRole.class);
            if (Arrays.asList(authentication.getAuthUser().getRoles()).contains(requiresRole.value())) {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresRole", Result.success().code(), Result.success().message());
                securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
                return joinPoint.proceed();
            } else {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresRole", ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION, "no permission");
                return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
            }
        } else {
            authentication = this.setAuthResultAndPrintLog("check-@Authenticated", authentication);
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }

    /**
     * RequiresRoleAndPerm
     */
    @Around("@within(org.potato.security.annotation.RequiresRoleAndPerm) || @annotation(org.potato.security.annotation.RequiresRoleAndPerm)")
    public Object requiresRoleAndPerm(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getAuthentication();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (targetClass.isAnnotationPresent(Anonymous.class) || targetMethod.isAnnotationPresent(Anonymous.class)) {
            return joinPoint.proceed();
        }

        if (authentication.isAuthenticated()) {
            RequiresRoleAndPerm requiresRoleAndPerm = targetMethod.isAnnotationPresent(RequiresRoleAndPerm.class) ? targetMethod.getAnnotation(RequiresRoleAndPerm.class) : targetClass.getAnnotation(RequiresRoleAndPerm.class);
            if (Arrays.asList(authentication.getAuthUser().getRoles()).contains(requiresRoleAndPerm.role()) && Arrays.asList(authentication.getAuthUser().getPerms()).contains(requiresRoleAndPerm.perm())) {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresRoleAndPerm", Result.success().code(), Result.success().message());
                securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
                return joinPoint.proceed();
            } else {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresRoleAndPerm", ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION, "no permission");
                return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
            }
        } else {
            authentication = this.setAuthResultAndPrintLog("check-@Authenticated", authentication);
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }

    /**
     * RequiresRoleOrPerm
     */
    @Around("@within(org.potato.security.annotation.RequiresRoleOrPerm) || @annotation(org.potato.security.annotation.RequiresRoleOrPerm)")
    public Object requiresRoleOrPerm(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getAuthentication();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (targetClass.isAnnotationPresent(Anonymous.class) || targetMethod.isAnnotationPresent(Anonymous.class)) {
            return joinPoint.proceed();
        }

        if (authentication.isAuthenticated()) {
            RequiresRoleOrPerm requiresRoleOrPerm = targetMethod.isAnnotationPresent(RequiresRoleOrPerm.class) ? targetMethod.getAnnotation(RequiresRoleOrPerm.class) : targetClass.getAnnotation(RequiresRoleOrPerm.class);
            if (Arrays.asList(authentication.getAuthUser().getRoles()).contains(requiresRoleOrPerm.role()) || Arrays.asList(authentication.getAuthUser().getPerms()).contains(requiresRoleOrPerm.perm())) {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresRoleOrPerm", Result.success().code(), Result.success().message());
                securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
                return joinPoint.proceed();
            } else {
                authentication = this.setAuthResultAndPrintLog("check-@RequiresRoleOrPerm", ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION, "no permission");
                return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
            }
        } else {
            authentication = this.setAuthResultAndPrintLog("check-@Authenticated", authentication);
            return securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
        }
    }


    public Authentication setAuthResultAndPrintLog(String authName, Authentication authentication) throws Throwable {
        return this.setAuthResultAndPrintLog(authName, authentication.getAuthResult().code(), authentication.getAuthResult().message());
    }
    public Authentication setAuthResultAndPrintLog(String authName, Integer authResultCode, String authResultMessage) throws Throwable {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        authentication.setAuthResult(Result.failure().code(authResultCode).message(authResultMessage));
        authentication.getRuntimeInstance().getLogInfo().put(authName, authResultMessage);
        authentication.getRuntimeInstance().getServletRequest().setAttribute(Constants.AUTHENTICATION, authentication);
        printLog(authentication);
        return authentication;
    }
    public void printLog(Authentication authentication) throws Throwable {
        logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authentication.getRuntimeInstance().getLogInfo()));
    }
}
