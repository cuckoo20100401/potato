package org.potato.security.mvc.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.AuthUser;
import org.potato.security.Authentication;
import org.potato.security.Constants;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.util.Result;
import org.potato.util.StringUtils;
import org.potato.util.web.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AuthenticationController
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
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
	 *     会同时返回token和refresh_token，若客户端对refresh_token没需求可以不关注，也不影响什么
	 * </p>
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping("/login")
	public Result login(HttpServletRequest request, String username, String password) {

		if (StringUtils.isNullOrEmpty(username)) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_EMPTY).message("username cannot be empty");
		}
		if (StringUtils.isNullOrEmpty(password)) {
			return Result.failure().code(ResponseCode.LOGIN_PASSWORD_IS_EMPTY).message("password cannot be empty");
		}
		
		AuthUser authUser = authenticationService.findAuthUserByUsername(username);
		if (authUser == null) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST).message("the account does not exist");
		}
		if (!authUser.getPassword().equals(StringUtils.encryptByAES(password))) {
			return Result.failure().code(ResponseCode.LOGIN_PASSWORD_IS_INCORRECT).message("the password is incorrect");
		}
		if (authUser.getStatus() == -1) {
			return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_DISABLED).message("the account is disabled");
		}

		authUser.setRoles(authenticationService.findAuthUserRolesById(authUser.getId()));
		authUser.setPerms(authenticationService.findAuthUserPermsById(authUser.getId()));
		authUser.setClientType(request.getParameter("clientType"));

		Map<String, Object> authUserX = new LinkedHashMap<>();
		authUserX.put("id", authUser.getId());
		authUserX.put("username", authUser.getUsername());
		authUserX.put("nickname", authUser.getNickname());
		authUserX.put("roles", authUser.getRoles());
		authUserX.put("perms", authUser.getPerms());

		String authenticationProviderName = securityConfiguration.getAuthenticationProvider().getClass().getSimpleName();
		if (authenticationProviderName.equals("CookieAndSessionAuthenticationProvider")) {
			request.getSession().setAttribute(Constants.AUTH_USER, authUser);
			return Result.success().addPayload("authUser", authUserX);
		} else if (authenticationProviderName.equals("CookieAndTokenAuthenticationProvider") || authenticationProviderName.equals("TokenAuthenticationProvider")) {
			authUserX.put("token", securityConfiguration.getTokenHandler().createToken(authUser));
			authUserX.put("refreshToken", securityConfiguration.getTokenHandler().createRefreshToken(authUser));
			return Result.success().addPayload("authUser", authUserX);
		} else {
			logger.error("configuration error, there is no such authentication provider["+authenticationProviderName+"]");
			return Result.failure().message("login failed");
		}
	}

	/**
	 * refreshToken
	 *
	 * @param refreshToken
	 * @return
	 */
	@PostMapping("/refreshToken")
	public Result refreshToken(String refreshToken) {

		AuthUser authUser = new AuthUser();
		authUser.setRefreshToken(refreshToken);
		Authentication authentication = new Authentication();
		authentication.setAuthUser(authUser);

		authentication = securityConfiguration.getTokenHandler().verifyAndParseRefreshToken(authentication);

		if (authentication.getAuthResult().isSuccess()) {

			authUser = authenticationService.findAuthUserByUsername(authentication.getAuthUser().getUsername());
			if (authUser == null) {
				return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST).message("the account does not exist");
			}
			if (authUser.getStatus() == -1) {
				return Result.failure().code(ResponseCode.LOGIN_USERNAME_IS_DISABLED).message("the account is disabled");
			}

			authUser.setRoles(authenticationService.findAuthUserRolesById(authUser.getId()));
			authUser.setPerms(authenticationService.findAuthUserPermsById(authUser.getId()));
			return Result.success().addPayload("token", securityConfiguration.getTokenHandler().createToken(authUser)).addPayload("refreshToken", securityConfiguration.getTokenHandler().createRefreshToken(authUser));
		}
		return Result.failure().code(authentication.getAuthResult().code()).message(authentication.getAuthResult().message());
	}
}