package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;

class SpecificFieldValueSetter<T> {
    private final String fieldName;
    private final String aliasFieldName;
    private final FieldValueSetter fieldValueSetter;

    private final String actualFieldName;

    SpecificFieldValueSetter(Field fieldToSet, String tableName) {
        fieldName = fieldToSet.getName();
        actualFieldName = fieldName;
        fieldValueSetter = new FieldValueSetter(fieldToSet);
        aliasFieldName = alias(fieldName, tableName);
    }

    SpecificFieldValueSetter(Field fieldToSet, String fieldName, String tableName) {
        fieldValueSetter = new FieldValueSetter(fieldToSet);
        this.fieldName = fieldName;
        aliasFieldName = alias(fieldName, tableName);
        actualFieldName = fieldToSet.getName();
    }

    public void accept(Object setFieldOn, Map<String, Object> row) {
        fieldValueSetter.accept(setFieldOn, row.get(aliasFieldName));
    }

    public void acceptExact(Object setFieldOn, Map<String, Object> row) {
        fieldValueSetter.accept(setFieldOn, row.get(actualFieldName));
    }

    @Override
    public String toString() {
        return "SpecificFieldValueSetter{" +
                "fieldName='" + fieldName + '\'' +
                ", aliasFieldName='" + aliasFieldName + '\'' +
                ", fieldValueSetter=" + fieldValueSetter +
                '}';
    }

    ColumnNameAndAlias fieldNameAndAlias(String tableName) {
        return new ColumnNameAndAlias(tableName + "." + fieldName, aliasFieldName);
    }

    private String alias(String fieldName, String tableName) {
        return tableName.concat("_").concat(fieldName);
    }
}
