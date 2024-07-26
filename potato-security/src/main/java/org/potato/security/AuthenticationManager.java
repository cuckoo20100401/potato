package org.potato.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.config.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     *     通过check()方法对当前请求进行安全检查，领取到检查结果报告，然后在随后的 AuthenticationAspect 中通过 AOP 对结果报告进行具体的校验和请求拦截
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
        authentication.getRuntimeInstance().getLogInfo().put("request-URI", currentRequestURI);
        String contextPath = request.getContextPath();
        if (!contextPath.equals("/")) {
            currentRequestURI = currentRequestURI.replaceFirst(contextPath, "");
        }

        String currentRequestPath = currentRequestURI;
        authentication.getRuntimeInstance().getLogInfo().put("request-path", currentRequestPath);

        authentication = securityConfiguration.getAuthenticationProvider().check(authentication);
        request.setAttribute(Constants.AUTHENTICATION, authentication);
        authentication.getRuntimeInstance().getFilterChain().doFilter(authentication.getRuntimeInstance().getServletRequest(), authentication.getRuntimeInstance().getServletResponse());
    }
}