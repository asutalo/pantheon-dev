package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.annotations.MySqlField;

import java.lang.reflect.Field;

/**
 * Function to convert a variable from an object into a MySqlValue
 */
public class NestedPrimaryFieldMySqlValue extends FieldMySqlValue {
    private final Field nestedPrimaryKeyField;

    public NestedPrimaryFieldMySqlValue(FieldMySqlValue nestedPrimaryKeyFieldMySqlValue, Field nestedPrimaryKeyField) {
        super();
        this.nestedPrimaryKeyField = nestedPrimaryKeyField;
        field = nestedPrimaryKeyFieldMySqlValue.getField();
        mysqlType = nestedPrimaryKeyFieldMySqlValue.getMysqlType();
        fieldName = fieldName(nestedPrimaryKeyField, nestedPrimaryKeyFieldMySqlValue.getFieldName());
        variableName = nestedPrimaryKeyField.getName();
        aliasName = nestedPrimaryKeyFieldMySqlValue.alias();
    }

    private String fieldName(Field field, String nestedFieldName) {
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        if (mySqlFieldInfo == null) {
            return field.getName();
        }
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return nestedFieldName;
        }

        return fieldName;
    }

    @Override
    public MySqlValue apply(Object valueOf) {
        try {
            Object nestedPrimaryValue = nestedPrimaryKeyField.get(valueOf);
            Object fieldValue = field.get(nestedPrimaryValue);
            return new MySqlValue(mysqlType, fieldName, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch field value for " + fieldName, e);
        }
    }
}
