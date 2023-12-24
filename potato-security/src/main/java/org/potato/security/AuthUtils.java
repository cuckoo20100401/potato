package org.potato.security;

import org.potato.util.Result;
import org.potato.util.web.ResponseCode;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthUtils {
	
	/**
	 * 校验认证规则
	 * @param authRule 系统配置的认证规则，从中提取此次访问必须的角色和权限
	 * @param userRoles 访问用户已有的角色列表
	 * @param userPerms 访问用户已有的权限列表
	 * @return 第一种情况：不匹配此认证规则模板，换下一个模板；第二种情况：有权限，认证通过；第三种情况：无权限，认证不通过
	 */
	public static Result validateAuthRuleForRolesAndPerms(String authRule, String[] userRoles, String[] userPerms) {
		
		String regex = "authc, roles\\[(.*)\\] and perms\\[(.*)\\]";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(authRule);
		if (matcher.find()) {
			
			int hasRoleCount = 0;
			int hasPermCount = 0;
			
			String[] requiredRoles = matcher.group(1).split(",");
			for (String requiredRole: requiredRoles) {
				if (Arrays.asList(userRoles).contains(requiredRole)) {
					hasRoleCount++;
				}
			}
			String[] requiredPerms = matcher.group(2).split(",");
			for (String requiredPerm: requiredPerms) {
				if (Arrays.asList(userPerms).contains(requiredPerm)) {
					hasPermCount++;
				}
			}
			
			if (hasRoleCount == requiredRoles.length && hasPermCount == requiredPerms.length) {
				return Result.success();
			} else {
				return Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("unauthorized request");
			}
		}
		return Result.nothing();
	}
	
	/**
	 * 校验认证规则
	 * @param authRule 系统配置的认证规则，从中提取此次访问必须的角色和权限
	 * @param userRoles 访问用户已有的角色列表
	 * @param userPerms 访问用户已有的权限列表
	 * @return 第一种情况：不匹配此认证规则模板，换下一个模板；第二种情况：有权限，认证通过；第三种情况：无权限，认证不通过
	 */
	public static Result validateAuthRuleForRolesOrPerms(String authRule, String[] userRoles, String[] userPerms) {
		
		String regex = "authc, roles\\[(.*)\\] or perms\\[(.*)\\]";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(authRule);
		if (matcher.find()) {
			
			int hasRoleCount = 0;
			int hasPermCount = 0;
			
			String[] requiredRoles = matcher.group(1).split(",");
			for (String requiredRole: requiredRoles) {
				if (Arrays.asList(userRoles).contains(requiredRole)) {
					hasRoleCount++;
				}
			}
			String[] requiredPerms = matcher.group(2).split(",");
			for (String requiredPerm: requiredPerms) {
				if (Arrays.asList(userPerms).contains(requiredPerm)) {
					hasPermCount++;
				}
			}
			
			if (hasRoleCount == requiredRoles.length || hasPermCount == requiredPerms.length) {
				return Result.success();
			} else {
				return Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("unauthorized request");
			}
		}
		return Result.nothing();
	}
	
	/**
	 * 校验认证规则
	 * @param authRule 系统配置的认证规则，从中提取此次访问必须的角色和权限
	 * @param userRoles 访问用户已有的角色列表
	 * @param userPerms 访问用户已有的权限列表
	 * @return 第一种情况：不匹配此认证规则模板，换下一个模板；第二种情况：有权限，认证通过；第三种情况：无权限，认证不通过
	 */
	public static Result validateAuthRuleForRoles(String authRule, String[] userRoles, String[] userPerms) {
		
		String regex = "authc, roles\\[(.*)\\]";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(authRule);
		if (matcher.find()) {
			
			int hasRoleCount = 0;
			
			String[] requiredRoles = matcher.group(1).split(",");
			for (String requiredRole: requiredRoles) {
				if (Arrays.asList(userRoles).contains(requiredRole)) {
					hasRoleCount++;
				}
			}
			
			if (hasRoleCount == requiredRoles.length) {
				return Result.success();
			} else {
				return Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("unauthorized request");
			}
		}
		return Result.nothing();
	}
	
	/**
	 * 校验认证规则
	 * @param authRule 系统配置的认证规则，从中提取此次访问必须的角色和权限
	 * @param userRoles 访问用户已有的角色列表
	 * @param userPerms 访问用户已有的权限列表
	 * @return 第一种情况：不匹配此认证规则模板，换下一个模板；第二种情况：有权限，认证通过；第三种情况：无权限，认证不通过
	 */
	public static Result validateAuthRuleForPerms(String authRule, String[] userRoles, String[] userPerms) {
		
		String regex = "authc, perms\\[(.*)\\]";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(authRule);
		if (matcher.find()) {
			
			int hasPermCount = 0;
			
			String[] requiredPerms = matcher.group(1).split(",");
			for (String requiredPerm: requiredPerms) {
				if (Arrays.asList(userPerms).contains(requiredPerm)) {
					hasPermCount++;
				}
			}
			
			if (hasPermCount == requiredPerms.length) {
				return Result.success();
			} else {
				return Result.failure().code(ResponseCode.AUTH_TOKEN_IS_NO_PERMISSION).message("unauthorized request");
			}
		}
		return Result.nothing();
	}
}