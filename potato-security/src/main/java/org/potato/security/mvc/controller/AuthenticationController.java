package org.potato.security.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
import org.potato.security.Constants;
import org.potato.security.SecurityUtils;
import org.potato.security.annotation.Security;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.mvc.entity.AuthUser;
import org.potato.security.mvc.entity.AuthUser4Login;
import org.potato.security.mvc.entity.RefreshToken;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.util.Result;
import org.potato.util.StringUtils;
import org.potato.util.web.ResponseCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AuthenticationController
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
@Security(anonymous = true)
public class AuthenticationController {

	private static final Logger logger = LogManager.getLogger(AuthenticationController.class);
	
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private SecurityConfiguration securityConfiguration;

	/**
	 * login
	 */
	@PostMapping({"/login", "/getToken"})
	public Result login(@RequestBody AuthUser4Login authUser4Login, HttpServletRequest request) {

		if (StringUtils.isNullOrEmpty(authUser4Login.getUsername())) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_REQUIRED).message("帐号不能为空");
		}
		if (StringUtils.isNullOrEmpty(authUser4Login.getPassword())) {
			return Result.failure().code(ResponseCode.LOGIN_PASSWORD_IS_REQUIRED).message("密码不能为空");
		}
		if (StringUtils.isNullOrEmpty(authUser4Login.getClientType())) {
			return Result.failure().code(ResponseCode.LOGIN_CLIENTTYPE_IS_REQUIRED).message("客户端类型不能为空");
		}
		
		AuthUser authUser = authenticationService.getAuthUserByUsername(authUser4Login.getUsername());
		if (authUser == null) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST).message("帐号不存在");
		}
		if (!authUser.getPassword().equals(StringUtils.encryptByAES(authUser4Login.getPassword()))) {
			return Result.failure().code(ResponseCode.LOGIN_PASSWORD_IS_INCORRECT).message("密码错误");
		}
		if (authUser.getStatus() != null && authUser.getStatus() == -1) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_DISABLED).message("帐号已被禁用");
		}

		List<String> authUserRoles = authenticationService.getAuthUserRolesById(authUser.getId());
		List<String> authUserPerms = authenticationService.getAuthUserPermsById(authUser.getId());
		Map<String, Object> authUserExtra = authenticationService.getAuthUserExtraById(authUser.getId());

		SecurityUser securityUser = SecurityUtils.getSecurityInfo().getSecurityUser();
		BeanUtils.copyProperties(authUser, securityUser);
		securityUser.setClientType(authUser4Login.getClientType());
		securityUser.setRoles(authUserRoles == null ? new ArrayList<>() : authUserRoles);
		securityUser.setPerms(authUserPerms == null ? new ArrayList<>() : authUserPerms);
		securityUser.setExtra(authUserExtra);

		Map<String, Object> authUserX = new LinkedHashMap<>();
		authUserX.put("id", securityUser.getId());
		authUserX.put("username", securityUser.getUsername());
		authUserX.put("nickname", securityUser.getNickname());
		authUserX.put("roles", securityUser.getRoles());
		authUserX.put("perms", securityUser.getPerms());

		String authenticationProviderName = securityConfiguration.getAuthenticationProvider().getClass().getSimpleName();
		if (authenticationProviderName.equals("CookieAndSessionAuthenticationProvider")) {
			request.getSession().setAttribute(Constants.SECURITY_USER, securityUser);
			securityConfiguration.getLoginSuccessHandler().onLoginSuccess(authUserX);
			return Result.success().addPayload("authUser", authUserX);
		} else if (authenticationProviderName.equals("CookieAndTokenAuthenticationProvider") || authenticationProviderName.equals("TokenAuthenticationProvider")) {
			authUserX.put("token", securityConfiguration.getTokenHandler().createToken(securityUser));
			if (securityConfiguration.getEnableRefreshToken()) {
				authUserX.put("refreshToken", securityConfiguration.getTokenHandler().createRefreshToken(securityUser));
			}
			securityConfiguration.getLoginSuccessHandler().onLoginSuccess(authUserX);
			return Result.success().addPayload("authUser", authUserX);
		} else {
			logger.error("configuration error, there is no such authentication provider["+authenticationProviderName+"]");
			return Result.failure().message("login failed");
		}
	}

	/**
	 * refreshToken
	 */
	@PostMapping("/refreshToken")
	public Result refreshToken(@RequestBody RefreshToken refreshToken) {

		SecurityInfo securityInfo = SecurityUtils.getSecurityInfo();
		SecurityUser securityUser = securityInfo.getSecurityUser();
		securityUser.setRefreshToken(refreshToken.getRefreshToken());

		securityInfo = securityConfiguration.getTokenHandler().verifyAndParseRefreshToken(securityInfo);

		if (securityInfo.getValidateResult().isSuccess()) {

			AuthUser authUser = authenticationService.getAuthUserByUsername(securityUser.getUsername());
			if (authUser == null) {
				return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST).message("帐号不存在");
			}
			if (authUser.getStatus() == -1) {
				return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_DISABLED).message("帐号已被禁用");
			}

			List<String> authUserRoles = authenticationService.getAuthUserRolesById(authUser.getId());
			List<String> authUserPerms = authenticationService.getAuthUserPermsById(authUser.getId());
			Map<String, Object> authUserExtra = authenticationService.getAuthUserExtraById(authUser.getId());

			BeanUtils.copyProperties(authUser, securityUser);
			securityUser.setRoles(authUserRoles == null ? new ArrayList<>() : authUserRoles);
			securityUser.setPerms(authUserPerms == null ? new ArrayList<>() : authUserPerms);
			securityUser.setExtra(authUserExtra);
			return Result.success().addPayload("token", securityConfiguration.getTokenHandler().createToken(securityUser)).addPayload("refreshToken", securityConfiguration.getTokenHandler().createRefreshToken(securityUser));
		}
		return securityInfo.getValidateResult();
	}
}