package com.eu.atit.mysql.service.filter;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.FieldMySqlValue;

import java.util.LinkedList;
import java.util.List;

//todo rename as it can be primary as well if primary.known == true
public class NonPrimaryMySqlValuesFilter<T> extends MySqlValuesFilter<T> {
    private final List<FieldMySqlValue> nonPrimaryKeyFieldMySqlValues;

    public NonPrimaryMySqlValuesFilter(List<FieldMySqlValue> nonPrimaryKeyFieldMySqlValues) {
        this.nonPrimaryKeyFieldMySqlValues = nonPrimaryKeyFieldMySqlValues;
    }

    @Override
    public LinkedList<MySqlValue> get(T object) {
        LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
        nonPrimaryKeyFieldMySqlValues.forEach(getter -> mySqlValues.add(getter.apply(object)));

        return mySqlValues;
    }
}
