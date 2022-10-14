package com.eu.atit.pantheon.annotation.data;

import com.eu.atit.pantheon.service.data.DataService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServedBy {
    Class<? extends DataService> dataService();
}