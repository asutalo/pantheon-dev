package com.eu.atit.mysql.service.filter;

import com.eu.atit.mysql.query.MySqlValue;

import java.util.LinkedList;

public abstract class MySqlValuesFilter<T> {
    public abstract LinkedList<MySqlValue> get(T object);
}
