package org.potato.security.mvc.service;

import org.potato.security.SecurityUser;

/**
 * AuthenticationService
 *
 * <p>
 *     class AuthenticationServiceImpl implements AuthenticationService
 * </p>
 *
 * <p>
 *     该接口中的方法需要项目去实现
 * </p>
 */
public interface AuthenticationService {

    SecurityUser getAuthUserByUsername(String username);

    String[] getAuthUserRolesById(String authUserId);
    String[] getAuthUserPermsById(String authUserId);
}