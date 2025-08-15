package org.potato.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.config.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * SecurityManager
 *
 * <p>
 *     当客户端请求进来时，我会把控整个认证流程，具体的认证工作由认证提供者完成
 * </p>
 */
@Component
public class SecurityManager {

    private static final Logger logger = LogManager.getLogger(SecurityManager.class);

    @Autowired
    private SecurityConfiguration securityConfiguration;

    /**
     * validate
     *
     * <p>
     *     通过 check() 方法对当前请求进行安全检查，领取到检查结果报告，然后在随后的 SecurityAspect 中通过 AOP 对结果报告进行具体的校验和请求拦截
     * </p>
     *
     * @param securityInfo
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    public void validate(SecurityInfo securityInfo) throws AuthenticationException, IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) securityInfo.getRuntimeInstance().getServletRequest();

        securityInfo.getRuntimeInstance().getLogInfo().put("requestURL", request.getRequestURL().toString());

        securityInfo = securityConfiguration.getAuthenticationProvider().check(securityInfo);
        request.setAttribute(Constants.SECURITY_INFO, securityInfo);
        securityInfo.getRuntimeInstance().getFilterChain().doFilter(securityInfo.getRuntimeInstance().getServletRequest(), securityInfo.getRuntimeInstance().getServletResponse());
    }
}