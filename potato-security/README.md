# potato-security

Authenticate and authorize, for horizontal scaling of servers.


## Design description

- 第一步，AuthenticationFilter像服务网关一样会拦截所有客户端请求，当拦截到一个请求时，认证过滤器会将当前的请求信息封装成Authentication对象传递给AuthenticationManager去认证
- 第二步，AuthenticationManager获取匹配的认证规则，比如anon、authc、roles and perms
- 第三步，AuthenticationManager会调用SecurityConfiguration中配置的认证提供者依次去认证anon、authc、roles and perms，当认证失败时会调用提前配置的AuthenticationFailureHandler方法，当认证成功时会调用AuthenticationSuccessHandler方法，并且将Authentication对象设置到request属性中供后面的Controller中使用，然后会继续往下走，比如authc认证完成后会往下走继续去认证roles and perms。另外在认证时传给认证提供者的也是Authentication对象，认证提供者认证完成后返回的也是Authentication对象，因为Authentication对象中不但包含了认证结果，还有其它有用的信息
- 注：SecurityContextHolder提供了在项目代码中任何地方都能获取到Authentication对象的能力，Authentication对象中就包含了当前的登录用户AuthUser对象


## Auth rule expression of url
实用的表达式已被支持，其余不是很实用的放到未来支持。
```properties
# 静态资源
/static/** = anon
# 接口请求
/api1/** = authc, roles[role1,role2]                             //supported
/api2/** = authc, perms[perm1,perm2]                             //supported
/api3/** = authc, roleOR[role1,role2]
/api4/** = authc, permOR[perm1,perm2]
/api5/** = authc, roles[role1,role2] or perms[perm1,perm2]       //supported
/api6/** = authc, roles[role1,role2] and perms[perm1,perm2]      //supported
/api7/** = authc, roleOR[role1,role2] or permOR[perm1,perm2]
/api8/** = authc, roleOR[role1,role2] and permOR[perm1,perm2]
# 剩余接口
/** = authc
```


## Permission control annotations
实用的注解已被支持，其余不是很实用的放到未来支持。
```java
package org.potato.security;

import java.util.LinkedHashMap;
import java.util.Map;

public class Example {

   // 设计方案一
   @RequiresAuthentication
   @RequiresRoles(value = {"role1", "role2"})
   @RequiresRolesOR(value = {"role1", "role2"})
   @RequiresRolesAndPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresRolesAndPerms(rolesOR = {"role1", "role2"}, permsOR = {"perm1", "perm2"})
   @RequiresRolesOrPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresRolesOrPerms(rolesOR = {"role1", "role2"}, permsOR = {"perm1", "perm2"})
   @RequiresPerms(value = {"perm1", "perm2"})
   @RequiresPermsOr(value = {"perm1", "perm2"})
   // 设计方案二
   @Authenticated
   @HasRole("role")
   @HasPerm("perm")
   @HasAllRoles(value = {"role1", "role2"})
   @HasAnyRoles(value = {"role1", "role2"})
   @HasAllPerms(value = {"perm1", "perm2"})
   @HasAnyPerms(value = {"perm1", "perm2"})
   @HasAllRolesOrAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAllRolesOrAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAnyRolesOrAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAnyRolesOrAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAllRolesAndAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAllRolesAndAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAnyRolesAndAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @HasAnyRolesAndAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   // 设计方案三（被采纳：实用的注解已被支持，其余不是很实用的放到未来支持）
   @RequiresAuthentication                                 //supported
   @RequiresAuthorization //也就是下面所有情况
   @RequiresRole("role")                                   //supported
   @RequiresPerm("perm")                                   //supported
   @RequiresRoleOrPerm(role = "role", perm = "perm")       //supported
   @RequiresRoleAndPerm(role = "role", perm = "perm")      //supported
   @RequiresAllRoles(value = {"role1", "role2"})
   @RequiresAnyRoles(value = {"role1", "role2"})
   @RequiresAllPerms(value = {"perm1", "perm2"})
   @RequiresAnyPerms(value = {"perm1", "perm2"})
   @RequiresAllRolesOrAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAllRolesOrAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAnyRolesOrAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAnyRolesOrAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAllRolesAndAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAllRolesAndAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAnyRolesAndAllPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   @RequiresAnyRolesAndAnyPerms(roles = {"role1", "role2"}, perms = {"perm1", "perm2"})
   public void getList() {
      System.out.println("Hello World!");
   }
}
```
