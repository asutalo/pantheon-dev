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
    /**
     * in an outward relationship the link is the name of the column in the parent that points to the main ID column of the child
     * in an inward relationship the link is the name of the column in the child that points to the main ID column of the parent
     * */

    String link() default "";

    boolean eager() default false;

    /**
     * used only for nested Lists, i.e. n:n relationship
     * the name of the connecting table, if not specified then it is inferred from the parent class_listed class
     * */
    String connection() default "";

    /**
     * used only for nested Lists, i.e. n:n relationship
     * represents the ID of the parent in the connecting table
     * if not specified then it is inferred as name of the parent class_ID column
     * */
    String from() default "";

    /**
     * used only for nested Lists, i.e. n:n relationship
     * represents the ID of the child in the connecting table
     * if not specified then it is inferred as name of the child class_ID column
     * */
    String to() default "";
}
