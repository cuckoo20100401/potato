# potato

Provide universal functions for your springboot project.


## Supports

- SpringBoot project.


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

import com.cuckoo.project.common.mapper.AuthenticationMapper;
import org.potato.security.SecurityUser;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.security.provider.TokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationMapper authenticationMapper;

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfiguration.builder()
                .setAuthenticationProvider(new TokenAuthenticationProvider())
                .setCreateTokenExpiredMinutes(60)
                .enableGlobalSecurity(true)
                .build();
    }

    @Bean
    public AuthenticationService authenticationService() {
       return new AuthenticationService() {
          @Override
          public AuthUser getAuthUserByUsername(String username) {
             return authenticationMapper.selectAuthUserByUsername(username);
          }

          @Override
          public List<String> getAuthUserRolesById(String authUserId) {
             return authenticationMapper.selectAuthUserRolesById(authUserId);
          }

          @Override
          public List<String> getAuthUserPermsById(String authUserId) {
             return authenticationMapper.selectAuthUserPermsById(authUserId);
          }

          @Override
          public Map<String, Object> getAuthUserExtraById(String authUserId) {
             Map<String, Object> extra = new HashMap<>();
             extra.put("cellphone", "13113026420");
             extra.put("telephone", "0755-1234567");
             extra.put("companyId", "uuid");
             extra.put("departmentId", "uuid");
             extra.put("hobbies", Arrays.asList("hobby1", "hobby2"));
             return extra;
          }
       };
    }
}
```
   - 最全配置
```java
package com.cuckoo.project.common.config;

import com.cuckoo.project.common.mapper.AuthenticationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.potato.security.SecurityUser;
import org.potato.security.SecurityInfo;
import org.potato.security.config.SecurityConfiguration;
import org.potato.security.handler.ValidationFailureHandler;
import org.potato.security.handler.ValidationSuccessHandler;
import org.potato.security.handler.TokenHandler;
import org.potato.security.mvc.service.AuthenticationService;
import org.potato.security.provider.TokenAuthenticationProvider;
import org.potato.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    private AuthenticationMapper authenticationMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfiguration.builder()
                .setAuthenticationProvider(new TokenAuthenticationProvider())
                .setCreateTokenSecret("1234")
                .setCreateTokenExpiredMinutes(60)
                .enableGlobalSecurity(true)
                .enableRefreshToken(true)
                .addTokenHandler(new TokenHandler() {
                    @Override
                    public String createToken(SecurityUser securityUser) {
                        return null;
                    }
                    @Override
                    public String createRefreshToken(SecurityUser securityUser) {
                        return null;
                    }
                    @Override
                    public SecurityInfo verifyAndParseToken(SecurityInfo securityInfo) {
                       return null;
                    }
                    @Override
                    public SecurityInfo verifyAndParseRefreshToken(SecurityInfo securityInfo) {
                        return null;
                    }
                })
                .addLoginSuccessHandler(new LoginSuccessHandler() {
                    @Override
                    public void onLoginSuccess(Map<String, Object> authUserX) {
                        // This will be sent to the client after successful login
                       SecurityUser securityUser = SecurityUtils.getSecurityInfo().getSecurityUser();
                       authUserX.put("companyId", securityUser.getExtra().get("companyId"));
                       authUserX.put("departmentId", securityUser.getExtra().get("departmentId"));
                       // Write login log
                       LogLogin logLogin = new LogLogin();
                       logLogin.setId(IdUtil.simpleUUID());
                       logLogin.setUserId(authUserX.get("id").toString());
                       logLogin.setUserName(authUserX.get("nickname").toString());
                       logLogin.setLoginTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                       logLogin.setRemark("该用户已登录");
                       logger.info(Constant.Log4j2.Marker.RabbitMQ.logLogin, logLogin);
                    }
                })
                .addValidationSuccessHandler(new ValidationSuccessHandler() {
                   @Override
                   public void onValidationSuccess(SecurityInfo securityInfo) {
                      HttpServletRequest request = (HttpServletRequest) securityInfo.getRuntimeInstance().getServletRequest();
                      request.setAttribute("contextPath", request.getContextPath());
                   }
                })
                .addValidationFailureHandler(new ValidationFailureHandler() {
                    @Override
                    public Object onValidationFailure(SecurityInfo securityInfo) {
                        return "error";
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
          public AuthUser getAuthUserByUsername(String username) {
             return authenticationMapper.selectAuthUserByUsername(username);
          }

          @Override
          public List<String> getAuthUserRolesById(String authUserId) {
             return authenticationMapper.selectAuthUserRolesById(authUserId);
          }

          @Override
          public List<String> getAuthUserPermsById(String authUserId) {
             return authenticationMapper.selectAuthUserPermsById(authUserId);
          }

          @Override
          public Map<String, Object> getAuthUserExtraById(String authUserId) {
              // 此处可以添加需要写入到token的额外属性
             Map<String, Object> extra = new HashMap<>();
             extra.put("cellphone", "13113026420");
             extra.put("telephone", "0755-1234567");
             extra.put("companyId", "uuid");
             extra.put("departmentId", "uuid");
             extra.put("hobbies", Arrays.asList("hobby1", "hobby2"));
             return extra;
          }
       };
    }
}
```
3. 应用
   - 如果启用了全局认证会对所有的Controller接口校验登录状态
   - 安全注解可用于类或方法上，并且方法级别的注解优先级大于类级别
   - 安全注解的所有参数都是可选的，无任何参数时的效果和启用全局认证是一样的，只校验登录状态
   - 安全注解中的逻辑参数默认值为AND
   - 如果需要匿名访问，只需要将匿名参数设置为真即可
```java
package com.cuckoo.project.core.controller;

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
        return sysUserService.getList(dto, pageNum, pageSize);
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

#### Example: use BaseDAO
- Category.java
```java
package com.cuckoo.project.common.domain.entity;

public class Category {

    private String id;
    private String name;

    /* some get and set methods */
}
```
- ElectronicBook.java
```java
package com.cuckoo.project.common.domain.entity;

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

@Service
public class ElectronicBookServiceImpl {

    @Autowired
    private ElectronicBookDAO electronicBookDAO;

	public void demo1() {
		int affectedRows = electronicBookDAO.insert(electronicBook);
        int affectedRows = electronicBookDAO.update(electronicBook);
        int affectedRows = electronicBookDAO.updateSelective(electronicBook);
        int affectedRows = electronicBookDAO.delete(id);
        int affectedRows = electronicBookDAO.delete(ids);
        ElectronicBook electronicBook = electronicBookDAO.findOne(id);
        List<ElectronicBook> electronicBooks = electronicBookDAO.findAll();
	}

	public void demo2() {
		PageInfo<ElectronicBook> pageInfo = electronicBookDAO.findList1();
        PageInfo<Map<String, Object>> pageInfo = electronicBookDAO.findList2();
	}
}
```
#### Example: use BaseMapper
- ElectronicBookMapper.java
```java
package com.cuckoo.project.common.mapper;

import com.cuckoo.project.common.domain.entity.ElectronicBook;

@Mapper
public interface ElectronicBookMapper extends BaseMapper<ElectronicBook> {

}
```
- ElectronicBookServiceImpl.java
```java
package com.cuckoo.project.core.service.impl;

@Service
public class ElectronicBookServiceImpl {

    @Autowired
    private ElectronicBookMapper electronicBookMapper;

	public void demo() {
       int affectedRows = electronicBookMapper.insert(electronicBook);
       int affectedRows = electronicBookMapper.insertBatch(electronicBooks);
       int affectedRows = electronicBookMapper.update(electronicBook);
       int affectedRows = electronicBookMapper.updateSelective(electronicBook);
       int affectedRows = electronicBookMapper.delete(id);
       int affectedRows = electronicBookMapper.deleteBatch(ids);
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
