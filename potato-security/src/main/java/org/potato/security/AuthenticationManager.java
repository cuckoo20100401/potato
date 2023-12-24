package org.potato.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.config.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * AuthenticationManager
 *
 * <p>
 *     当客户端请求进来时，我会把控整个认证流程，具体的认证工作由认证提供者完成
 * </p>
 */
@Component
public class AuthenticationManager {

    private static final Logger logger = LogManager.getLogger(AuthenticationManager.class);

    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * authenticate
     *
     * <p>
     *     认证登录状态、角色和权限
     * </p>
     *
     * @param authentication
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    public void authenticate(Authentication authentication) throws AuthenticationException, IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();

        String currentRequestURI = request.getRequestURI();
        authentication.getRuntimeInstance().getLogInfo().put("current-request-URI", currentRequestURI);
        String contextPath = request.getContextPath();
        if (!contextPath.equals("/")) {
            currentRequestURI = currentRequestURI.replaceFirst(contextPath, "");
        }

        String currentRequestPath = currentRequestURI;
        authentication.getRuntimeInstance().getLogInfo().put("current-request-path", currentRequestPath);

        AntPathMatcher pathMatcher = new AntPathMatcher();
        String matchedAuthPattern = securityConfiguration.getAuthRules().keySet().stream().filter(authPattern -> pathMatcher.match(authPattern, currentRequestPath)).findFirst().orElse(null);
        authentication.getRuntimeInstance().getLogInfo().put("matched-auth-pattern", matchedAuthPattern);
        if (matchedAuthPattern != null) {
            //* obtain matched auth rule
            String matchedAuthRule = securityConfiguration.getAuthRules().get(matchedAuthPattern);
            authentication.getRuntimeInstance().getLogInfo().put("matched-auth-rule", matchedAuthRule);
            authentication.setAuthRule(matchedAuthRule);
            //* validate anon
            if (matchedAuthRule.trim().equals("anon")) {
                authentication.getRuntimeInstance().getLogInfo().put("auth-anon", "ok");
                logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authentication.getRuntimeInstance().getLogInfo()));
                securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
                request.setAttribute(Constants.AUTHENTICATION, authentication);
                authentication.getRuntimeInstance().getFilterChain().doFilter(authentication.getRuntimeInstance().getServletRequest(), authentication.getRuntimeInstance().getServletResponse());
                return;
            }
            //* validate authc
            if (matchedAuthRule.indexOf("authc") != -1) {
                authentication = securityConfiguration.getAuthenticationProvider().validateAuthc(authentication);
                if (authentication.getAuthResult().isSuccess()) {
                    //continue run code of filter, eg:validate roles and perms
                } else {
                    logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authentication.getRuntimeInstance().getLogInfo()));
                    securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
                    return;
                }
            }
            //* validate roles and perms
            authentication = securityConfiguration.getAuthenticationProvider().validateRolesAndPerms(authentication);
            if (!authentication.getAuthResult().isNothing()) {
                logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authentication.getRuntimeInstance().getLogInfo()));
                if (authentication.getAuthResult().isSuccess()) {
                    securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
                    request.setAttribute(Constants.AUTHENTICATION, authentication);
                    authentication.getRuntimeInstance().getFilterChain().doFilter(authentication.getRuntimeInstance().getServletRequest(), authentication.getRuntimeInstance().getServletResponse());
                } else {
                    securityConfiguration.getAuthenticationFailureHandler().onAuthenticationFailure(authentication);
                }
                return;
            }
        }

        //* will run the code: (1)when authRules has only authc and be passed,(2)when has not matched AuthPattern
        logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(authentication.getRuntimeInstance().getLogInfo()));
        securityConfiguration.getAuthenticationSuccessHandler().onAuthenticationSuccess(authentication);
        request.setAttribute(Constants.AUTHENTICATION, authentication);
        authentication.getRuntimeInstance().getFilterChain().doFilter(authentication.getRuntimeInstance().getServletRequest(), authentication.getRuntimeInstance().getServletResponse());
        return;
    }
}