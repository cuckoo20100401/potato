package org.potato.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.potato.util.Result;

import java.util.Map;

/**
 * Authentication
 *
 * <p>
 *     贯穿认证的始终，包括整个请求过程
 * </p>
 */
public class Authentication {

    private AuthUser authUser;
    private Result authResult;
    private Boolean authenticated;
    private RuntimeInstance runtimeInstance;

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }

    public Result getAuthResult() {
        return authResult;
    }

    public void setAuthResult(Result authResult) {
        this.authResult = authResult;
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