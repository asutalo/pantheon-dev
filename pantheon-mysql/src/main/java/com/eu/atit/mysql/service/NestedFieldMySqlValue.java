package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;

import java.lang.reflect.Field;

/**
 * Function to convert a variable from an object into a MySqlValue
 */
public class NestedFieldMySqlValue extends FieldMySqlValue {
    public NestedFieldMySqlValue(FieldMySqlValue nestedPrimaryKeyFieldMySqlValue, Field parentField) {
        super(nestedPrimaryKeyFieldMySqlValue.getField(), parentField.getName(), nestedPrimaryKeyFieldMySqlValue.getVariableName(), nestedPrimaryKeyFieldMySqlValue.getMysqlType());
        fieldName = fieldName(parentField, nestedPrimaryKeyFieldMySqlValue.getFieldName());
    }

    private String fieldName(Field field, String nestedFieldName) {
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        if (mySqlFieldInfo == null) {
            System.out.println("There was a parent without MySqlField annotation... check NestedFieldMySqlValue.class");
            return field.getName();
        }
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return field.getName() + "_" + nestedFieldName;
        }

        return fieldName;
    }
}
