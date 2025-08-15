package org.potato.security.mvc.service;

import org.potato.security.mvc.entity.AuthUser;

import java.util.List;
import java.util.Map;

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

    AuthUser getAuthUserByUsername(String username);

    List<String> getAuthUserRolesById(String authUserId);
    List<String> getAuthUserPermsById(String authUserId);

    Map<String, Object> getAuthUserExtraById(String authUserId);
}