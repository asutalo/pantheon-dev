package com.eu.atit.mysql.service.filter;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.FieldMySqlValue;
import com.eu.atit.mysql.service.FieldValueGetter;
import com.eu.atit.pantheon.helper.Pair;

import java.util.LinkedList;
import java.util.List;

public class NonPrimaryNestedMySqlValuesFilter<T> extends NonPrimaryMySqlValuesFilter<T> {
    private final List<Pair<FieldMySqlValue, FieldValueGetter>> nestedFieldMySqlValues;

    public NonPrimaryNestedMySqlValuesFilter(List<FieldMySqlValue> nonPrimaryKeyFieldMySqlValues, List<Pair<FieldMySqlValue, FieldValueGetter>> nestedFieldMySqlValues) {
        super(nonPrimaryKeyFieldMySqlValues);
        this.nestedFieldMySqlValues = nestedFieldMySqlValues;
    }

    @Override
    public LinkedList<MySqlValue> get(T object) {
        LinkedList<MySqlValue> mySqlValues = super.get(object);
        nestedFieldMySqlValues.forEach(nestedFieldMySqlValue -> mySqlValues.add(nestedFieldMySqlValue.left().apply(nestedFieldMySqlValue.right().apply(object))));
        return mySqlValues;
    }
}
