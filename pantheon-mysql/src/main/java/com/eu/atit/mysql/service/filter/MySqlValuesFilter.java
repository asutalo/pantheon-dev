package com.eu.atit.mysql.service.filter;

import com.eu.atit.mysql.query.MySqlValue;

import java.util.LinkedList;

//todo why was this abstract class before???
public interface MySqlValuesFilter<T> {
    LinkedList<MySqlValue> get(T object);
}
