# potato

Contains universal functions for java web project.


## Supports

- Spring java web project.


## Install
```xml
<dependency>
    <groupId>org.potato</groupId>
    <artifactId>potato-all</artifactId>
    <version>3.0.0</version>
</dependency>
```


## Modules

### potato-util

Universal utils.

- DTO
- FileUtils
- ImageUtils
- IPUtils
- MathUtils
- Result
- StringUtils
- ValidateUtils

### potato-util-web

Universal web utils.

- PageInfo
- RequestUtils
- ResponseCode
- ResponseUtils
- VisitorUtils

### potato-util-db

Universal database utils.

- EntityTableTransformUtils
- IDUtils
- JDBC

### potato-security

Authenticate and authorize, for horizontal scaling of servers.

#### Configuration
1. 配置SpringBoot的启动文件
   - @ComponentScan(basePackages = {"com.cuckoo.project.core", "com.cuckoo.project.common", "org.potato.security"}) //开启Spring注解
   - @ServletComponentScan(basePackages = {"org.potato.security"}) //开启Servlet注解
```java
package com.cuckoo.project.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * 需要开启框架中的Spring注解和Servlet注解
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.cuckoo.project.core", "com.cuckoo.project.common", "org.potato.security"})
@ServletComponentScan(basePackages = {"org.potato.security"})
@EnableScheduling
@EnableWebSocket
public class CoreApplication {

   public static void main(String[] args) {
      SpringApplication.run(CoreApplication.class, args);
   }

}
```
2. 安全配置
   - 最简配置
```java
package com.cuckoo.project.common.config;

import com.cuckoo.project.common.dao.SysUserDAO;
import org.potato.security.AuthUser;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.security.provider.TokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Autowired
    private SysUserDAO sysUserDAO;

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfiguration.builder()
                .setAuthenticationProvider(new TokenAuthenticationProvider())
                .setCreateTokenExpiredMinutes(60)
                .enableGlobalAuthenticated(true)
                .build();
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService() {
            @Override
            public AuthUser findAuthUserByUsername(String username) {
                return sysUserDAO.findByUsername(username);
            }

            @Override
            public String[] findAuthUserRolesById(String id) {
                return sysUserDAO.findRolesById(id);
            }

            @Override
            public String[] findAuthUserPermsById(String id) {
                return sysUserDAO.findPermsById(id);
            }
        };
    }
}
```
   - 最全配置
```java
package com.cuckoo.project.common.config;

import com.cuckoo.project.common.dao.SysUserDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.potato.security.AuthUser;
import org.potato.security.Authentication;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.handler.AuthenticationFailureHandler;
import org.potato.security.handler.AuthenticationSuccessHandler;
import org.potato.security.handler.TokenHandler;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.security.provider.TokenAuthenticationProvider;
import org.potato.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全配置
 *
 * <p>定义安全配置（必须的）</p>
 * <ol>
 *     <li>设置认证提供者（必须的）</li>
 *     <li>设置各种token属性（可选的）</li>
 *     <li>启用全局认证（可选的）</li>
 *     <li>启用刷新Token（可选的）</li>
 *     <li>添加各种处理器（可选的）</li>
 * </ol>
 * <p>定义认证服务（必须的）</p>
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private SysUserDAO sysUserDAO;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfiguration.builder()
                .setAuthenticationProvider(new TokenAuthenticationProvider())
                .setCreateTokenSecret("1234")
                .setCreateTokenExpiredMinutes(60)
                .enableGlobalAuthenticated(true)
                .enableRefreshToken(true)
                .addTokenHandler(new TokenHandler() {
                    @Override
                    public String createToken(AuthUser authUser) {
                        return null;
                    }

                    @Override
                    public String createRefreshToken(AuthUser authUser) {
                        return null;
                    }

                    @Override
                    public Authentication verifyAndParseToken(Authentication authentication) {

                        // 1.verify token
                        String accessToken = authentication.getAuthUser().getAccessToken();

                        // 2.parse token
                        AuthUser authUser = authentication.getAuthUser();
                        authUser.setId("id");
                        authUser.setUsername("username");
                        authUser.setNickname("nickname");
                        /* set other properties */
                        authUser.setRoles(new String[]{"guest"});
                        authUser.setPerms(new String[]{"sys:user:view", "sys:user:edit"});

                        // 3.update token, is optional
                        HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
                        response.addCookie(new Cookie("Token", "token-created"));

                        // 4.set auth result
                        authentication.setAuthResult(Result.success());
                        return authentication;
                    }

                    @Override
                    public Authentication verifyAndParseRefreshToken(Authentication authentication) {
                        return null;
                    }
                })
                .addLoginSuccessHandler(new LoginSuccessHandler() {
                    @Override
                    public void onLoginSuccess(AuthUser authUser, Map<String, Object> authUserX) {
                        // add extra attributes send to client
                        authUserX.put("extraAttribute1", "value1");
                        authUserX.put("extraAttribute2", "value2");
                        // save authUserSessionStatus to redis and set the expiration time
                        String redisKey = Constant.Redis.keyPrefix.authUser + authUser.getUsername();
                        Map<String, Object> redisValue = new LinkedHashMap<>();
                        redisValue.put("authUserId", authUser.getId());
                        redisValue.put("loginTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        redisValue.put("sessionDurationMinutes", Constant.authUserSessionDurationMinutes);
                        redisValue.put("sessionRemainingDurationMinutes", Constant.authUserSessionDurationMinutes);
                        redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofMinutes(Constant.authUserSessionDurationMinutes));
                        // do something
                    }
                })
                .addAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(Authentication authentication) {
                        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
                        request.setAttribute("currentRequestURI", request.getRequestURI());
                    }
                })
                .addAuthenticationFailureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(Authentication authentication) {
                        try {
                            HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
                            response.setContentType("application/json;charset=utf-8");
                            Result result = Result.failure().code(authentication.getAuthResult().code()).message(authentication.getAuthResult().message());
                            response.getWriter().write(objectMapper.writeValueAsString(result));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addLogHandler(new LogHandler() {
                   @Override
                   public void onLog(Map<String, String> log) {
                      logger.info(log);
                   }
                })
                .build();
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService() {
            @Override
            public AuthUser findAuthUserByUsername(String username) {
                return sysUserDAO.findByUsername(username);
            }
   
            @Override
            public String[] findAuthUserRolesById(String id) {
                return sysUserDAO.findRolesById(id);
            }
   
            @Override
            public String[] findAuthUserPermsById(String id) {
                return sysUserDAO.findPermsById(id);
            }
        };
    }
}
```
3. 应用
   - 如果启用了全局认证，可以省略@Authenticated注解
   - RequiresXX开头的注解只能配置一个
   - 方法级别的注解优先级大于类级别
   - @Anonymous注解的优先级最高，只要添加了匿名注解，认证时会忽略其它所有注解（如果类中某个方法需要匿名访问，可以仅给该方法加匿名注解即可）
```java
package com.cuckoo.project.core.controller;

@RestController
@RequestMapping("/core/sys/user")
@Anonymous                                                    //表示访问整个Controller是匿名的，不做任何检验
@Authenticated                                                //表示访问整个Controller是需要认证的，即必须登录
@RequiresRole("admin")                                        //表示访问整个Controller需要admin角色
@RequiresPerm("sys:user:view")                                //表示访问整个Controller需要sys:user:view权限
@RequiresRoleOrPerm(role = "admin", perm = "sys:user:view")   //表示访问整个Controller需要admin角色或sys:user:view权限
@RequiresRoleAndPerm(role = "admin", perm = "sys:user:view")  //表示访问整个Controller需要admin角色和sys:user:view权限
public class SysUserController {

    @GetMapping("/getList")
    @Anonymous                                                    //表示访问这个方法是匿名的，不做任何检验
    @Authenticated                                                //表示访问这个方法是需要认证的，即必须登录
    @RequiresRole("admin")                                        //表示访问这个方法需要admin角色
    @RequiresPerm("sys:user:view")                                //表示访问这个方法需要sys:user:view权限
    @RequiresRoleOrPerm(role = "admin", perm = "sys:user:view")   //表示访问这个方法需要admin角色或sys:user:view权限
    @RequiresRoleAndPerm(role = "admin", perm = "sys:user:view")  //表示访问这个方法需要admin角色和sys:user:view权限
    public Result getList() {
        return sysUserService.findList(sysUser, pageNum, pageSize);
    }
}
```

#### Enable log
- 当使用SpringBoot默认日志时，在application.properties中添加如下配置：
```properties
logging.level.org.potato.security = DEBUG
```
- 当使用Apache Log4j2时，在log4j2.xml中添加如下配置：
```xml
<Logger name="org.potato.security" level="debug" additivity="false">
    <AppenderRef ref="Console"/>
</Logger>
```
- 注：当配置了LogHandler时，需要自行处理日志，可以打印或保存日志。上述配置只在没配置LogHandler时才生效。

#### FAQ
1. 解决跨域
   - 使用SpringBoot的跨域配置
   - Controller中使用@CrossOrigin注解
   - 使用nginx的跨域配置

### potato-jdbc

A sample data persistence tool.

#### Example
- Category.java
```java
package com.cuckoo.project.common.entity;

public class Category {

    private String id;
    private String name;

    /* some get and set methods */
}
```
- ElectronicBook.java
```java
package com.cuckoo.project.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class ElectronicBook implements Serializable {

	private String id;
	private String name;
	private String authorName;
	private Integer size;
	private BigDecimal price1;
	private Double price2;
	private Date createDate;
	private Timestamp createTime;
	private Category category;

   /* some get and set methods */
}
```
- ElectronicBookDAO.java
```java
package com.cuckoo.project.common.dao;

import com.cuckoo.project.common.entity.ElectronicBook;
import org.potato.util.web.PageInfo;

import java.util.Map;

public interface ElectronicBookDAO extends BaseDAO<ElectronicBook> {

    PageInfo<ElectronicBook> findList1();

    PageInfo<Map<String, Object>> findList2();
}
```
- ElectronicBookDAOImpl.java
```java
package com.cuckoo.project.common.dao;

import com.cuckoo.project.common.entity.ElectronicBook;
import org.potato.util.web.PageInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Repository
public class ElectronicBookDAOImpl extends BaseDAOImpl<ElectronicBook> implements ElectronicBookDAO {
    
    @Override
    public PageInfo<ElectronicBook> findList1() {
        String sql = "select id as \"id\", name as \"name\", author_name as \"authorName\", size as \"size\", price1 as \"price1\", price2 as \"price2\", create_date as \"createDate\", create_time as \"createTime\" from electronic_book";
        PageInfo<ElectronicBook> pageInfo = jdbcTemplateExt.findList(sql, null, 1, 3, new RowMapper<ElectronicBook>() {
            @Override
            public ElectronicBook mapRow(ResultSet rs, int rowNum) throws SQLException {
                ElectronicBook entity = new ElectronicBook();
                entity.setId(rs.getString("id"));
                entity.setName(rs.getString("name"));
                entity.setAuthorName(rs.getString("authorName"));
                entity.setSize(rs.getInt("size"));
                entity.setPrice1(rs.getBigDecimal("price1"));
                entity.setPrice2(rs.getDouble("price2"));
                entity.setCreateDate(rs.getDate("createDate"));
                entity.setCreateTime(rs.getTimestamp("createTime"));
                return entity;
            }
        });
        return pageInfo;
    }

    @Override
    public PageInfo<Map<String, Object>> findList2() {
        String sql = "select id as \"id\", name as \"name\", author_name as \"authorName\", size as \"size\", price1 as \"price1\", price2 as \"price2\", create_date as \"createDate\", create_time as \"createTime\" from electronic_book";
        return jdbcTemplateExt.findList(sql, null, 1, 5);
    }
}
```
- ElectronicBookServiceImpl.java
```java
package com.cuckoo.project.core.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cuckoo.project.common.dao.ElectronicBookDAO;
import com.cuckoo.project.common.dao.ElectronicBookDAOImpl;
import com.cuckoo.project.common.entity.ElectronicBook;
import org.potato.util.web.PageInfo;

@Service
public class ElectronicBookServiceImpl {

    @Autowired
    private ElectronicBookDAO electronicBookDAO;

	public void insert() {
		ElectronicBook electronicBook = new ElectronicBook();
		electronicBook.setId("eb000010");
		electronicBook.setName("天龙八部");
		electronicBook.setAuthorName("桥峰");
		electronicBook.setSize(1024);
		electronicBook.setPrice1(new BigDecimal(104.52));
		electronicBook.setPrice2(204.52);
		electronicBook.setCreateDate(Date.valueOf(LocalDate.now()));
		electronicBook.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
		int affectedRowCount = electronicBookDAO.insert(electronicBook);
		System.out.println("affectedRowCount: " + affectedRowCount);
	}
	
	public void update() {
		ElectronicBook dbElectronicBook = electronicBookDAO.findOne("eb000005");
		if (dbElectronicBook != null) {
			dbElectronicBook.setAuthorName("潘金莲");
			int affectedRowCount = electronicBookDAO.update(dbElectronicBook);
			System.out.println("affectedRowCount: " + affectedRowCount);
		}
	}

	public void updateSelective() {
		ElectronicBook dbElectronicBook = electronicBookDAO.findOne("eb000004");
		if (dbElectronicBook != null) {
			dbElectronicBook.setAuthorName("孙悟空");
			int affectedRowCount = electronicBookDAO.updateSelective(dbElectronicBook);
			System.out.println("affectedRowCount: " + affectedRowCount);
		}
	}

	public void delete() {
		System.out.println("affectedRowCount: " + electronicBookDAO.delete("eb000001"));
		System.out.println("affectedRowCount: " + electronicBookDAO.delete(new String[]{"eb000002", "eb000003"}));
	}

	public void findOne() {
		System.out.println("-->" + electronicBookDAO.findOne("eb000010"));
		System.out.println("-->" + electronicBookDAO.findOne("eb111111"));
	}

	public void findAll() {
		List<ElectronicBook> electronicBooks = electronicBookDAO.findAll();
		for (ElectronicBook electronicBook: electronicBooks) {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(electronicBook));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void findList1() {
		PageInfo<ElectronicBook> pageInfo = electronicBookDAO.findList1();
		for (ElectronicBook electronicBook: pageInfo.getList()) {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(electronicBook));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void findList2() {
		PageInfo<Map<String, Object>> pageInfo = electronicBookDAO.findList2();
		for (Map<String, Object> row: pageInfo.getList()) {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(row));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
```

### potato-validation

Validate parameters for API.

#### Example
```java
package com.cuckoo.project.core.controller;

import org.potato.util.Result;
import org.potato.util.web.DTO;
import org.potato.validation.rule.IntegerRule;
import org.potato.validation.rule.StringRule;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Validation组件的两种用法示例
 */
@RestController
public class UserController {

   /**
    * 测试 @RequestParam 接收参数
    *
    * @param title
    * @param age
    * @return
    */
   @RequestMapping("/testRequestParam")
   public Result testRequestParam(
           @RequestParam String title,
           @RequestParam Integer age) {

      //step 校验参数
      //substep Validator组件校验参数
      Result result = Validator.builder()
              .validateString("title", title, StringRule.notNullAndEmpty(), "title不能为空")
              .validateString("title", title, StringRule.lengthEqual(8), "title长度必须等于8")
              .validateString("title", title, StringRule.lengthGreaterThan(10), "title长度必须大于10")
              .validateString("title", title, StringRule.lengthLessThan(20), "title长度必须小于20")
              .validateString("title", title, StringRule.lengthBetween(10, 20), "title长度必须在10和20之间")
              .validateInteger("age", age, IntegerRule.notNull(), "age不能为空")
              .validateInteger("age", age, IntegerRule.lengthEqual(8), "age长度必须等于8")
              .result();
      if (result.isFailure()) {
         return result;
      }
      //substep 自定义校验参数（弥补Validator组件的不足）
      if (title.contains("@")) {
         return Result.failure().message("title不能包含@符号");
      }

      //step 执行业务
      try {
         userService.process(title, age);
         return Result.success();
      } catch (Exception e) {
         e.printStackTrace();
         return Result.failure();
      }
   }

   /**
    * 测试 @RequestBody 接收参数
    *
    * @param dto
    * @return
    */
   @RequestMapping("/testRequestBody")
   public Result testRequestBody(@RequestBody DTO dto) {

      //step 校验参数
      //substep Validator组件校验参数
      Result result = Validator.builder(dto)
              .validateString("title", StringRule.notNullAndEmpty(), "title不能为空")
              .validateString("title", StringRule.lengthGreaterThan(10), "title长度必须大于10")
              .validateString("title", StringRule.lengthLessThan(20), "title长度必须小于20")
              .validateString("title", StringRule.lengthBetween(10, 20), "title长度必须在10和20之间")
              .validateInteger("age", IntegerRule.notNull(), "age不能为空")
              .validateInteger("age", IntegerRule.lengthEqual(8), "age长度必须等于8")
              .result();
      if (result.isFailure()) {
         return result;
      }
      //substep 自定义校验参数（弥补Validator组件的不足）
      if (dto.getAsString("title").contains("@")) {
         return Result.failure().message("title不能包含@符号");
      }

      //step 执行业务
      try {
         userService.process(dto.getAsString("title"), dto.getAsInt("age"));
         return Result.success();
      } catch (Exception e) {
         e.printStackTrace();
         return Result.failure();
      }
   }
}
```
