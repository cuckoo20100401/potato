package org.potato.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.potato.util.Result;

import java.util.Map;

/**
 * SecurityInfo
 *
 * <p>
 *     贯穿认证的始终，包括整个请求过程
 * </p>
 */
public class SecurityInfo {

    private SecurityUser securityUser;
    private Result validateResult;
    private Boolean authenticated;
    private RuntimeInstance runtimeInstance;

    public SecurityUser getSecurityUser() {
        return securityUser;
    }

    public void setSecurityUser(SecurityUser securityUser) {
        this.securityUser = securityUser;
    }

    public Result getValidateResult() {
        return validateResult;
    }

    public void setValidateResult(Result validateResult) {
        this.validateResult = validateResult;
    }

    public Boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public RuntimeInstance getRuntimeInstance() {
        return runtimeInstance;
    }

    public void setRuntimeInstance(RuntimeInstance runtimeInstance) {
        this.runtimeInstance = runtimeInstance;
    }

    public class RuntimeInstance {

        private ServletRequest servletRequest;
        private ServletResponse servletResponse;
        private FilterChain filterChain;
        private Map<String, String> logInfo;

        public ServletRequest getServletRequest() {
            return servletRequest;
        }

        public void setServletRequest(ServletRequest servletRequest) {
            this.servletRequest = servletRequest;
        }

        public ServletResponse getServletResponse() {
            return servletResponse;
        }

        public void setServletResponse(ServletResponse servletResponse) {
            this.servletResponse = servletResponse;
        }

        public FilterChain getFilterChain() {
            return filterChain;
        }

        public void setFilterChain(FilterChain filterChain) {
            this.filterChain = filterChain;
        }

        public Map<String, String> getLogInfo() {
            return logInfo;
        }

        public void setLogInfo(Map<String, String> logInfo) {
            this.logInfo = logInfo;
        }
    }
}