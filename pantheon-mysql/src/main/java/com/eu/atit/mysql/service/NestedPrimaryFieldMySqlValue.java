package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.service.annotations.MySqlField;

import java.lang.reflect.Field;

/**
 * Function to convert a variable from an object into a MySqlValue
 */
public class NestedPrimaryFieldMySqlValue extends FieldMySqlValue {
    private final FieldValueGetter nestedPrimaryKeyValueGetter;

    public NestedPrimaryFieldMySqlValue(FieldMySqlValue nestedPrimaryKeyFieldMySqlValue, Field nestedPrimaryKeyField) {
        super(nestedPrimaryKeyFieldMySqlValue.getField(), nestedPrimaryKeyField.getName(), nestedPrimaryKeyFieldMySqlValue.alias(), nestedPrimaryKeyFieldMySqlValue.getMysqlType());
        nestedPrimaryKeyValueGetter = new FieldValueGetter(nestedPrimaryKeyField);
        fieldName = fieldName(nestedPrimaryKeyField, nestedPrimaryKeyFieldMySqlValue.getFieldName());
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
        Object nestedPrimaryValue = nestedPrimaryKeyValueGetter.apply(valueOf);
        Object fieldValue = fieldValueGetter.apply(nestedPrimaryValue);
        return new MySqlValue(mysqlType, fieldName, fieldValue);
    }
}
