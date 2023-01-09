package com.eu.atit.mysql.service;

import com.eu.atit.pantheon.helper.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//used when merging objects, specific to fetching an object that has a list of nested objects inside
class SpecificFieldValueOverride<T> implements BiConsumer<T, T> {
    private final String fieldName;
    private final String aliasFieldName;
    private final FieldValueSetter<T> fieldValueSetter;
    private final FieldValueGetter<T> fieldValueGetter;

    private final boolean isList;

    SpecificFieldValueOverride(Field fieldToSet, String tableName) {
        fieldName = fieldToSet.getName();
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);

        Type genericType = fieldToSet.getGenericType();
        isList = genericType.getTypeName().contains("List");

        fieldValueGetter = new FieldValueGetter<>(fieldToSet);
        aliasFieldName = alias(fieldName, tableName);
    }

    SpecificFieldValueOverride(Field fieldToSet, String fieldName, String tableName) {
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        fieldValueGetter = new FieldValueGetter<>(fieldToSet);
        Type genericType = fieldToSet.getGenericType();
        isList = genericType.getTypeName().contains("List");

        this.fieldName = fieldName;
        aliasFieldName = alias(fieldName, tableName);
    }

    @Override
    public void accept(T setFieldOn, T getValueFrom) {
        if(!isList){
            fieldValueSetter.accept(setFieldOn, fieldValueGetter.apply(getValueFrom));
        } else {
            List<T> originalList = (List<T>) fieldValueGetter.apply(setFieldOn);
            List<T> additionalList = (List<T>) fieldValueGetter.apply(getValueFrom);
            originalList.addAll(additionalList);
            fieldValueSetter.accept(setFieldOn, originalList);
        }
    }

    String getFieldName() {
        return fieldName;
    }

    String getAliasFieldName() {
        return aliasFieldName;
    }

    Pair<String, String> fieldNameAndAlias() {
        return new Pair<>(fieldName, aliasFieldName);
    }

    Pair<String, String> fieldNameAndAlias(String tableName) {
        return new Pair<>(tableName + "." + fieldName, aliasFieldName);
    }

    ColumnNameAndAlias fieldNameAndAlias2(String tableName) {
        return new ColumnNameAndAlias(tableName + "." + fieldName, aliasFieldName);
    }

    private String alias(String fieldName, String tableName) {
        return tableName.concat("_").concat(fieldName);
    }
}
