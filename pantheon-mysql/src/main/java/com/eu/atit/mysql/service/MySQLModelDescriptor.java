package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.google.inject.TypeLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLModelDescriptor<T> {
    private final MySQLServiceFieldsProvider mySQLServiceFieldsProvider;
    private final Class<T> modelClass;
    private Instantiator<T> instantiator;
    private FieldMySqlValue<T> primaryKeyFieldMySqlValue;

    private final Map<String, FieldMySqlValue<T>> fieldMySqlValueMap = new HashMap<>(); //will include primary key

    private List<FieldMySqlValue<T>> nonPrimaryKeyFieldMySqlValues;
    /*
     * used to initialise a full POJO including primary key FROM select statement with table names and joins in mind
     * */
    private List<SpecificFieldValueSetter<T>> specificFieldValueSetters;
    private List<SpecificFieldValueOverride<T>> specificFieldValueOverrides;
    private SpecificFieldValueSetter<T> primaryKeyValueSetter;
    private List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters;
    private ArrayList<ColumnNameAndAlias> columnsAndAliases;
    private Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //no primary key included but will include not annotated fields as well
    /*
     * only used to set ID on the pojo which is returned from here
     * */
    private FieldValueSetter<T> primaryKeyFieldValueSetter;
    private String tableName;

    //full traversal down all EAGER nested classes
    private List<JoinInfo> joinInfos;

    public MySQLModelDescriptor(MySQLServiceFieldsProvider mySQLServiceFieldsProvider, TypeLiteral<T> modelTypeLiteral) {
        modelClass = (Class<T>) modelTypeLiteral.getRawType();
        mySQLServiceFieldsProvider.validateClass(modelClass);
        this.mySQLServiceFieldsProvider = mySQLServiceFieldsProvider;
    }

    public void init() {
        tableName = mySQLServiceFieldsProvider.getTableName(modelClass);
        instantiator = mySQLServiceFieldsProvider.getInstantiator(modelClass);
        nonPrimaryKeyFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(modelClass);
        primaryKeyFieldMySqlValue = mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(modelClass);
        primaryKeyFieldValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(modelClass);
        specificFieldValueSetters = mySQLServiceFieldsProvider.getSpecificFieldValueSetters(modelClass);
        specificFieldValueOverrides = mySQLServiceFieldsProvider.getSpecificFieldValueOverrides(modelClass);
        primaryKeyValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyValueSetter(modelClass);
        fieldMySqlValueMap.put(primaryKeyFieldMySqlValue.alias(), primaryKeyFieldMySqlValue);
        nonPrimaryKeyFieldMySqlValues.forEach(fieldMySqlValue -> fieldMySqlValueMap.put(fieldMySqlValue.alias(), fieldMySqlValue));
        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(modelClass);

        specificNestedFieldValueSetters = mySQLServiceFieldsProvider.getSpecificNestedFieldValueSetters(modelClass);
        joinInfos = mySQLServiceFieldsProvider.getJoinInfos(modelClass);

        columnsAndAliases = mySQLServiceFieldsProvider.getColumnsAndAliases(tableName.toLowerCase(), specificFieldValueSetters, joinInfos);
    }

    public MySQLServiceFieldsProvider getMySQLServiceFieldsProvider() {
        return mySQLServiceFieldsProvider;
    }

    public Class<T> getModelClass() {
        return modelClass;
    }

    public Instantiator<T> getInstantiator() {
        return instantiator;
    }

    public FieldMySqlValue<T> getPrimaryKeyFieldMySqlValue() {
        return primaryKeyFieldMySqlValue;
    }

    public Map<String, FieldMySqlValue<T>> getFieldMySqlValueMap() {
        return fieldMySqlValueMap;
    }

    public List<FieldMySqlValue<T>> getNonPrimaryKeyFieldMySqlValues() {
        return nonPrimaryKeyFieldMySqlValues;
    }

    public List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters() {
        return specificFieldValueSetters;
    }

    public List<SpecificFieldValueOverride<T>> getSpecificFieldValueOverrides() {
        return specificFieldValueOverrides;
    }

    public SpecificFieldValueSetter<T> getPrimaryKeyValueSetter() {
        return primaryKeyValueSetter;
    }

    public List<SpecificNestedFieldValueSetter<T>> getSpecificNestedFieldValueSetters() {
        return specificNestedFieldValueSetters;
    }

    public ArrayList<ColumnNameAndAlias> getColumnsAndAliases() {
        return columnsAndAliases;
    }

    public Map<String, FieldValueSetter<T>> getAllExceptPrimaryFieldValueSetterMap() {
        return allExceptPrimaryFieldValueSetterMap;
    }

    public FieldValueSetter<T> getPrimaryKeyFieldValueSetter() {
        return primaryKeyFieldValueSetter;
    }

    public String getTableName() {
        return tableName;
    }

    public List<JoinInfo> getJoinInfos() {
        return joinInfos;
    }
}
