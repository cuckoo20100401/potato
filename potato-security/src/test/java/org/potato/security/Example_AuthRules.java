package org.potato.security;

import java.util.LinkedHashMap;

/**
 * Security模块组件设计思路：
 * 第一步，AuthenticationFilter像服务网关一样会拦截所有客户端请求，当拦截到一个请求时，认证过滤器会将当前的请求信息封装成Authentication对象传递给AuthenticationManager去认证
 * 第二步，AuthenticationManager获取匹配的认证规则，比如anon、authc、roles and perms
 * 第三步，AuthenticationManager会调用SecurityConfiguration中配置的认证提供者依次去认证anon、authc、roles and perms，当认证失败时会调用提前配置的AuthenticationFailureHandler方
 * 法，当认证成功时会调用AuthenticationSuccessHandler方法，并且将Authentication对象设置到request属性中供后面的Controller中使用，然后会继续往下走，比如authc认证完成后会往下走继续去认证
 * roles and perms。另外在认证时传给认证提供者的也是Authentication对象，认证提供者认证完成后返回的也是Authentication对象，因为Authentication对象中不但包含了认证结果，还有其它有用的信息
 *
 * 注：SecurityContextHolder提供了在项目代码中任何地方都能获取到Authentication对象的能力，Authentication对象中就包含了当前的登录用户AuthUser对象
 */
public class Example_AuthRules {
	
	protected LinkedHashMap<String, String> initAuthRules() {
		
		LinkedHashMap<String, String> authRules = new LinkedHashMap<>();
		//静态资源
		authRules.put("/static/**", "anon");
		//认证模块
		authRules.put("/auth/login", "anon");
		authRules.put("/auth/refreshToken", "anon");
		//系统管理
		authRules.put("/sys/role/**", "authc, roles[超级管理员] or perms[MENU-系统管理-角色管理]");
		authRules.put("/sys/user/**", "authc, roles[超级管理员] or perms[MENU-系统管理-用户管理]");
		//消费管理
		authRules.put("/consumption/**", "authc, roles[超级管理员] or perms[MENU-消费管理-主要消费]");
		authRules.put("/consumption_other/**", "authc, roles[超级管理员] or perms[MENU-消费管理-其它消费]");
		authRules.put("/consumption_statistics/**", "authc, roles[超级管理员] or perms[MENU-消费管理-消费统计]");
		//未来扩展
//		this.authRules.put("/teacher/**", "authc, roles[admin1,admin2]"); //supported
//		this.authRules.put("/teacher/**", "authc, perms[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]"); //supported
//		this.authRules.put("/teacher/**", "authc, roleOR[admin1,admin2]");
//		this.authRules.put("/teacher/**", "authc, permOR[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]");
//		this.authRules.put("/teacher/**", "authc, roles[admin1,admin2] and perms[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]"); //supported
//		this.authRules.put("/teacher/**", "authc, roles[admin1,admin2] or perms[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]"); //supported
//		this.authRules.put("/teacher/**", "authc, roleOR[admin1,admin2] and permOR[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]");
//		this.authRules.put("/teacher/**", "authc, roleOR[admin1,admin2] or permOR[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]");
		//其它
		authRules.put("/**", "authc");
		return authRules;
	}
}