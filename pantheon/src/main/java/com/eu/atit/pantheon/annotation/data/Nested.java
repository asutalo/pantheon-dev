package com.eu.atit.pantheon.annotation.data;

public @interface Nested {

    boolean outward() default false;
    boolean inward() default false;

    String link() default "";

    boolean eager() default false;
}
