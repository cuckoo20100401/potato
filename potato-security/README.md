# v1.x.x

## Design description

### 第一版：
- 第一步，AuthenticationFilter像服务网关一样会拦截所有客户端请求，当拦截到一个请求时，认证过滤器会将当前的请求信息封装成Authentication对象传递给AuthenticationManager去认证
- 第二步，AuthenticationManager获取匹配的认证规则，比如anon、authc、roles and perms
- 第三步，AuthenticationManager会调用SecurityConfiguration中配置的认证提供者依次去认证anon、authc、roles and perms，当认证失败时会调用提前配置的AuthenticationFailureHandler方法，当认证成功时会调用AuthenticationSuccessHandler方法，并且将Authentication对象设置到request属性中供后面的Controller中使用，然后会继续往下走，比如authc认证完成后会往下走继续去认证roles and perms。另外在认证时传给认证提供者的也是Authentication对象，认证提供者认证完成后返回的也是Authentication对象，因为Authentication对象中不但包含了认证结果，还有其它有用的信息

### 第二版（实现注解功能后）：
- 第一步，AuthenticationFilter像服务网关一样会拦截所有客户端请求，当拦截到一个请求时，认证过滤器会将当前的请求信息封装成Authentication对象传递给AuthenticationManager去认证
- 第二步，AuthenticationManager会调用SecurityConfiguration中配置的认证提供者的check()方法进行检查，然后领取到一张检查结果报告随身携带着。需要注意的是此处仅仅只是检查，拿到检查结果报告而已，比如校验和解析token，并不做权限的判断和拦截
- 第三步，AuthenticationAspect会根据是否启用全局认证、以及添加的权限控制注解，对上一步获取的检查结果报告进行具体的权限判断和请求拦截。相应的在校验成功或校验失败时会触发AuthenticationSuccessHandler和AuthenticationFailureHandler中的回调方法
- SecurityContextHolder提供了在项目代码中任何地方都能获取到Authentication对象的能力，Authentication对象中就包含了当前的登录用户AuthUser对象

### 附

#### Auth rule expression of url

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

#### Permission control annotations

实用的注解已被支持，其余不是很实用的放到未来支持。
```java
public class Example {
   @Anonymous                                              //supported
   @Authenticated                                          //supported
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
   public Object getList() {
      return "Hello World!";
   }
}
```

## Important change log

- upgrade jdk11 to jdk17


# v2.x.x

## Design description

- 同 v1.x.x 中的第二版

## Important change log

- upgrade springboot2 to springboot3
- add addLogHandler()


# v3.x.x

## Design description

- 第一步，SecurityFilter 会拦截所有客户端请求，当拦截到一个请求时，过滤器会将当前的请求信息封装成 SecurityInfo 对象传递给 SecurityManager 去校验
- 第二步，SecurityManager 会调用 SecurityConfiguration 中配置的认证提供者的 check() 方法进行检查，然后领取到一张检查结果报告随身携带着。需要注意的是此处仅仅只是检查，拿到检查结果报告而已，比如校验和解析token，并不做权限的判断和拦截
- 第三步，SecurityAspect 会根据是否启用全局认证、以及添加的权限控制注解，对上一步获取的检查结果报告进行具体的权限判断和请求拦截。相应的在校验成功或校验失败时会触发 ValidationSuccessHandler 和 ValidationFailureHandler 中的回调方法
- 第四步，SecurityUtils 提供了在项目代码中任何地方都能获取到 SecurityInfo 对象的能力，SecurityInfo 对象中就包含了当前的认证用户信息

## Important change log

### Change file name

- AuthenticationFilter to SecurityFilter
- Authentication to SecurityInfo
- AuthenticationManager to SecurityManager
- AuthenticationAspect to SecurityAspect
- AuthenticationSuccessHandler to ValidationSuccessHandler
- AuthenticationFailureHandler to ValidationFailureHandler
- SecurityContextHolder to SecurityUtils

### Change permission control annotations

```java
@RestController
@RequestMapping("/core/sys/user")
@Security(
    anonymous = false,
    roles = @RequiresRoles(value = {"guest","user","vipUser","superVipUser"}, logical = Logical.AND),
    perms = @RequiresPerms(value = {"sys:user:view","sys:user:edit"}, logical = Logical.AND),
    logical = Logical.AND
)
public class SysUserController {

    @GetMapping("/getList")
    @Security(
        anonymous = false,
        roles = @RequiresRoles(value = {"guest","user","vipUser","superVipUser"}, logical = Logical.AND),
        perms = @RequiresPerms(value = {"sys:user:view","sys:user:edit"}, logical = Logical.AND),
        logical = Logical.AND
    )
    public Result getList() {
        return sysUserService.getList(sysUser, pageNum, pageSize);
    }
}
```

### Others

- The SecurityUser class has already inherited from the AuthUser class, so the SysUser entity class in the project no longer needs to inherit from AuthUser.
- The SecurityUser class has added an `extra` property to include additional information about the authenticated user.




