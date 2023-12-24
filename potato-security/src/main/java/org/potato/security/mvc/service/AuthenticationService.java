package org.potato.security.mvc.service;

import org.potato.security.AuthUser;

/**
 * AuthenticationService
 *
 * <p>
 *     class SysUserService impl AuthenticationService
 * </p>
 *
 * <p>
 *     该接口中的方法需要项目去实现
 * </p>
 */
public interface AuthenticationService {

    AuthUser findAuthUserByUsername(String username);

    String[] findAuthUserRolesById(String id);
    String[] findAuthUserPermsById(String id);
}