package com.eu.atit.mysql.service.filter;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.MySQLModelDescriptor;

import java.util.LinkedList;

public abstract class MySqlValuesFilter<T>{
    final MySQLModelDescriptor<T> mySQLModelDescriptor;

    MySqlValuesFilter(MySQLModelDescriptor<T> mySQLModelDescriptor) {
        this.mySQLModelDescriptor = mySQLModelDescriptor;
    }

    public abstract LinkedList<MySqlValue> get(T object);
}
