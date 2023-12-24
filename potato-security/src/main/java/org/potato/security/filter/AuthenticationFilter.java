package org.potato.security.filter;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import org.potato.security.Authentication;
import org.potato.security.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AuthenticationFilter
 *
 * <p>
 *     我会拦截所有请求，并将请求信息封装成Authentication对象传递给AuthenticationManager去认证
 * </p>
 */
@WebFilter(urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

	private FilterConfig filterConfig;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		Authentication.RuntimeInstance runtimeInstance = new Authentication().new RuntimeInstance();
		runtimeInstance.setServletRequest(servletRequest);
		runtimeInstance.setServletResponse(servletResponse);
		runtimeInstance.setFilterChain(filterChain);
		runtimeInstance.setLogInfo(new LinkedHashMap<>());

		Authentication authentication = new Authentication();
		authentication.setRuntimeInstance(runtimeInstance);

		authenticationManager.authenticate(authentication);
	}
}