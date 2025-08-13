package org.potato.security.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
import org.potato.security.Constants;
import org.potato.security.annotation.Security;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.mvc.entity.AuthUser;
import org.potato.security.mvc.entity.RefreshToken;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.util.Result;
import org.potato.util.StringUtils;
import org.potato.util.web.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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
	 *
	 * <p>
	 *     会同时返回token和refreshToken，若客户端对refresh_token没需求可以不关注，也不影响什么
	 * </p>
	 */
	@PostMapping({"/getToken", "/login"})
	public Result login(@RequestBody AuthUser authUser, HttpServletRequest request) {

		if (StringUtils.isNullOrEmpty(authUser.getUsername())) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_REQUIRED).message("帐号不能为空");
		}
		if (StringUtils.isNullOrEmpty(authUser.getPassword())) {
			return Result.failure().code(ResponseCode.LOGIN_PASSWORD_IS_REQUIRED).message("密码不能为空");
		}
		if (StringUtils.isNullOrEmpty(authUser.getClientType())) {
			return Result.failure().code(ResponseCode.LOGIN_CLIENTTYPE_IS_REQUIRED).message("客户端类型不能为空");
		}
		
		SecurityUser securityUser = authenticationService.getAuthUserByUsername(authUser.getUsername());
		if (securityUser == null) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST).message("帐号不存在");
		}
		if (!securityUser.getPassword().equals(StringUtils.encryptByAES(authUser.getPassword()))) {
			return Result.failure().code(ResponseCode.LOGIN_PASSWORD_IS_INCORRECT).message("密码错误");
		}
		if (securityUser.getStatus() != null && securityUser.getStatus() == -1) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_DISABLED).message("帐号已被禁用");
		}

		securityUser.setClientType(authUser.getClientType());
		securityUser.setRoles(authenticationService.getAuthUserRolesById(securityUser.getId()));
		securityUser.setPerms(authenticationService.getAuthUserPermsById(securityUser.getId()));

		Map<String, Object> authUserX = new LinkedHashMap<>();
		authUserX.put("id", securityUser.getId());
		authUserX.put("username", securityUser.getUsername());
		authUserX.put("nickname", securityUser.getNickname());
		authUserX.put("roles", securityUser.getRoles());
		authUserX.put("perms", securityUser.getPerms());

		String authenticationProviderName = securityConfiguration.getAuthenticationProvider().getClass().getSimpleName();
		if (authenticationProviderName.equals("CookieAndSessionAuthenticationProvider")) {
			request.getSession().setAttribute(Constants.SECURITY_USER, securityUser);
			securityConfiguration.getLoginSuccessHandler().onLoginSuccess(securityUser, authUserX);
			return Result.success().addPayload("authUser", authUserX);
		} else if (authenticationProviderName.equals("CookieAndTokenAuthenticationProvider") || authenticationProviderName.equals("TokenAuthenticationProvider")) {
			authUserX.put("token", securityConfiguration.getTokenHandler().createToken(securityUser));
			if (securityConfiguration.getEnableRefreshToken()) {
				authUserX.put("refreshToken", securityConfiguration.getTokenHandler().createRefreshToken(securityUser));
			}
			securityConfiguration.getLoginSuccessHandler().onLoginSuccess(securityUser, authUserX);
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

		SecurityUser securityUser = new SecurityUser();
		securityUser.setRefreshToken(refreshToken.getRefreshToken());
		SecurityInfo securityInfo = new SecurityInfo();
		securityInfo.setSecurityUser(securityUser);

		securityInfo = securityConfiguration.getTokenHandler().verifyAndParseRefreshToken(securityInfo);

		if (securityInfo.getValidateResult().isSuccess()) {

			securityUser = authenticationService.getAuthUserByUsername(securityInfo.getSecurityUser().getUsername());
			if (securityUser == null) {
				return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST).message("帐号不存在");
			}
			if (securityUser.getStatus() == -1) {
				return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_DISABLED).message("帐号已被禁用");
			}

			securityUser.setRoles(authenticationService.getAuthUserRolesById(securityUser.getId()));
			securityUser.setPerms(authenticationService.getAuthUserPermsById(securityUser.getId()));
			return Result.success().addPayload("token", securityConfiguration.getTokenHandler().createToken(securityUser)).addPayload("refreshToken", securityConfiguration.getTokenHandler().createRefreshToken(securityUser));
		}
		return securityInfo.getValidateResult();
	}
}