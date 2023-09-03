package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;

import java.lang.reflect.Field;

/**
 * Function to convert a variable from an object into a MySqlValue
 */
public class NestedPrimaryFieldMySqlValue extends FieldMySqlValue {
    public NestedPrimaryFieldMySqlValue(FieldMySqlValue nestedPrimaryKeyFieldMySqlValue, Field parentField) {
        super();
        field = nestedPrimaryKeyFieldMySqlValue.getField();
        mysqlType = nestedPrimaryKeyFieldMySqlValue.getMysqlType();
        fieldName = fieldName(parentField, nestedPrimaryKeyFieldMySqlValue.getFieldName());
        variableName = parentField.getName();
        aliasName = nestedPrimaryKeyFieldMySqlValue.getVariableName();
    }

    private String fieldName(Field field, String nestedFieldName) {
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        if (mySqlFieldInfo == null) {
            return field.getName();
        }
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return nestedFieldName ;
        }

        return fieldName;
    }
}
