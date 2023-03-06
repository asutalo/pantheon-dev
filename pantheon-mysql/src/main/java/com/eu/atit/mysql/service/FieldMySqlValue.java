package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Function to convert a variable from an object into a MySqlValue
 */
public class FieldMySqlValue {
    private final Field field;//todo replace with FieldValueGetter to simplify
    private final String fieldName;
    private final String variableName;
    private final String aliasName;
    private final MysqlType mysqlType;

    /**
     * @param field     reflection of the variable that is used to fetch the value for MySqlValue
     * @param mysqlType desired MySql type for the value of the field to have
     */
    FieldMySqlValue(Field field, MysqlType mysqlType, String tableName) {
        this.field = field;
        this.mysqlType = mysqlType;
        fieldName = field.getName();
        variableName = fieldName;
        aliasName = alias(tableName, fieldName);
    }

    public FieldMySqlValue(MySQLModelDescriptor<?> modelDescriptor, Field parentField) {
        FieldMySqlValue nestedPrimaryKeyFieldMySqlValue = modelDescriptor.getPrimaryKeyFieldMySqlValue();
        this.field = nestedPrimaryKeyFieldMySqlValue.getField();
        this.mysqlType = nestedPrimaryKeyFieldMySqlValue.getMysqlType();
        this.fieldName = fieldName(parentField);
        variableName = parentField.getName();
        aliasName = nestedPrimaryKeyFieldMySqlValue.getVariableName();
    }

    private String fieldName(Field field) {
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        if (mySqlFieldInfo == null) {
            return field.getName();
        }
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return field.getName();
        }

        return fieldName;
    }

    /**
     * @param field     reflection of the variable that is used to fetch the value for MySqlValue
     * @param mysqlType desired MySql type for the value of the field to have
     */
    FieldMySqlValue(Field field, MysqlType mysqlType, String fieldName, String tableName) {
        this.field = field;
        this.mysqlType = mysqlType;
        this.fieldName = fieldName;
        variableName = field.getName();
        aliasName = alias(tableName, fieldName);
    }

    private String alias(String tableName, String fieldName) {
        return tableName + "." + fieldName;
    }

    public MySqlValue apply(Object valueOf) {
        try {
            Object fieldValue = field.get(valueOf);
            return new MySqlValue(mysqlType, fieldName, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch field value", e);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldMySqlValue that = (FieldMySqlValue) o;

        if (!Objects.equals(field, that.field)) return false;
        if (!Objects.equals(fieldName, that.fieldName)) return false;
        if (!Objects.equals(variableName, that.variableName)) return false;
        if (!Objects.equals(aliasName, that.aliasName)) return false;
        return mysqlType == that.mysqlType;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        result = 31 * result + (variableName != null ? variableName.hashCode() : 0);
        result = 31 * result + (aliasName != null ? aliasName.hashCode() : 0);
        result = 31 * result + (mysqlType != null ? mysqlType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldMySqlValue{" +
               "field=" + field +
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
