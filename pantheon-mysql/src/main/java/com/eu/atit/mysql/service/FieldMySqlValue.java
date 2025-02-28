package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;
import com.google.common.base.Objects;
import com.mysql.cj.MysqlType;

import java.lang.reflect.Field;

/**
 * Function to convert a variable from an object into a MySqlValue
 */
//todo can we simplify this class and the extensions of it?
public class FieldMySqlValue {
    Field field;//reflection of the variable that is used to fetch the value for MySqlValue
    FieldValueGetter fieldValueGetter;
    String fieldName;
    String variableName;
    String aliasName;
    MysqlType mysqlType;    //desired MySql type for the value of the field to have

    FieldMySqlValue(Field field, MysqlType mysqlType, String tableName) {
        fieldValueGetter = new FieldValueGetter(field);
        this.field = field;
        this.mysqlType = mysqlType;
        fieldValueGetter = new FieldValueGetter(field);
        fieldName = field.getName();
        variableName = fieldName;
        aliasName = alias(tableName, fieldName);
    }

    FieldMySqlValue(Field field, MysqlType mysqlType, String fieldName, String tableName) {
        fieldValueGetter = new FieldValueGetter(field);
        this.field = field;
        this.mysqlType = mysqlType;
        this.fieldName = fieldName;
        variableName = field.getName();
        aliasName = alias(tableName, fieldName);
    }

    public FieldMySqlValue(Field field, String variableName, String aliasName, MysqlType mysqlType) {
        fieldValueGetter = new FieldValueGetter(field);
        this.field = field;
        this.variableName = variableName;
        this.aliasName = aliasName;
        this.mysqlType = mysqlType;
    }

    private String alias(String tableName, String fieldName) {
        return tableName + "." + fieldName;
    }

    public MySqlValue apply(Object valueOf) {
        Object fieldValue = fieldValueGetter.apply(valueOf);
        return new MySqlValue(mysqlType, fieldName, fieldValue);
    }

    MySqlValue of(Object val) {
        return new MySqlValue(mysqlType, aliasName, val);
    }

    String getFieldName() {
        return fieldName;
    }

    String getVariableName() {
        return variableName;
    }

    String alias() {
        return aliasName;
    }

    @IgnoreCoverage
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FieldMySqlValue that = (FieldMySqlValue) o;
        return Objects.equal(field, that.field) && Objects.equal(fieldValueGetter, that.fieldValueGetter) && Objects.equal(fieldName, that.fieldName) && Objects.equal(variableName, that.variableName) && Objects.equal(aliasName, that.aliasName) && mysqlType == that.mysqlType;
    }

    @IgnoreCoverage
    @Override
    public int hashCode() {
        return Objects.hashCode(field, fieldValueGetter, fieldName, variableName, aliasName, mysqlType);
    }

    @IgnoreCoverage
    @Override
    public String toString() {
        return "FieldMySqlValue{" +
                "field=" + field +
                ", fieldValueGetter=" + fieldValueGetter +
                ", fieldName='" + fieldName + '\'' +
                ", variableName='" + variableName + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", mysqlType=" + mysqlType +
                '}';
    }

    Field getField() {
        return field;
    }

    MysqlType getMysqlType() {
        return mysqlType;
    }
}
