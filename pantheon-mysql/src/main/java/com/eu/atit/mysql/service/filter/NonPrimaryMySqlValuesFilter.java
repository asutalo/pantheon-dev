package com.eu.atit.mysql.service.filter;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.MySQLModelDescriptor;

import java.util.LinkedList;

public class NonPrimaryMySqlValuesFilter<T> extends MySqlValuesFilter<T>{
    public NonPrimaryMySqlValuesFilter(MySQLModelDescriptor<T> mySQLModelDescriptor) {
        super(mySQLModelDescriptor);
    }

    @Override
    public LinkedList<MySqlValue> get(T object) {
        LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
        mySQLModelDescriptor.getNonPrimaryKeyFieldMySqlValues().forEach(getter -> mySqlValues.add(getter.apply(object)));

        return mySqlValues;
    }
}
