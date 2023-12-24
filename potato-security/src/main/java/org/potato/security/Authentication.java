package org.potato.security;

import org.potato.util.Result;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Map;

/**
 * Authentication
 *
 * <p>
 *     贯穿认证的始终，包括整个请求过程
 * </p>
 */
public class Authentication {

    private String authRule;
    private AuthUser authUser;
    private Result authResult;
    private Boolean isAuthenticated;
    private RuntimeInstance runtimeInstance;

    public String getAuthRule() {
        return authRule;
    }

    public void setAuthRule(String authRule) {
        this.authRule = authRule;
    }

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

    public Boolean getAuthenticated() {
        return isAuthenticated;
    }

    public Authentication setAuthenticated(Boolean authenticated) {
        this.authResult = null;
        this.runtimeInstance.servletRequest = null;
        this.runtimeInstance.servletResponse = null;
        this.runtimeInstance.filterChain = null;
        this.runtimeInstance.logInfo = null;
        this.isAuthenticated = authenticated;
        return this;
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