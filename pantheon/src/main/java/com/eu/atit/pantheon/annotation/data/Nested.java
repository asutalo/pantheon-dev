package com.eu.atit.pantheon.annotation.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nested {

    boolean outward() default false;
    boolean inward() default false;

    String link() default "";

    boolean eager() default false;

    String connection() default "";
    String from() default "";
    String to() default "";
}
