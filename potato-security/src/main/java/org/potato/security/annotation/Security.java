package org.potato.security.annotation;

import org.potato.security.enumerate.Logical;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Security {
    boolean anonymous() default false;
    RequiresRoles roles() default @RequiresRoles;
    RequiresPerms perms() default @RequiresPerms;
    Logical logical() default Logical.DEFAULT;
}
