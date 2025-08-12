package org.potato.security.annotation;

import org.potato.security.enumerate.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPerms {
    String[] value() default {};
    Logical logical() default Logical.DEFAULT;
}
