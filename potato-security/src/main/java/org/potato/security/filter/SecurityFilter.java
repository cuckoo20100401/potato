package org.potato.security.filter;

import java.io.IOException;
import java.util.LinkedHashMap;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import org.potato.security.SecurityInfo;
import org.potato.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SecurityFilter
 *
 * <p>
 *     我会拦截所有请求，并将请求信息封装成 SecurityInfo 对象传递给 SecurityManager 去校验
 * </p>
 */
@WebFilter(urlPatterns = {"/*"})
public class SecurityFilter implements Filter {

	private FilterConfig filterConfig;

	@Autowired
	private SecurityManager securityManager;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		SecurityInfo.RuntimeInstance runtimeInstance = new SecurityInfo().new RuntimeInstance();
		runtimeInstance.setServletRequest(servletRequest);
		runtimeInstance.setServletResponse(servletResponse);
		runtimeInstance.setFilterChain(filterChain);
		runtimeInstance.setLogInfo(new LinkedHashMap<>());

		SecurityInfo securityInfo = new SecurityInfo();
		securityInfo.setRuntimeInstance(runtimeInstance);

		securityManager.validate(securityInfo);
	}
}