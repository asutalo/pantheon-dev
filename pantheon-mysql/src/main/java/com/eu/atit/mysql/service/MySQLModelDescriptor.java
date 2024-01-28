package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.filter.MySqlValuesFilter;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.google.inject.TypeLiteral;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MySQLModelDescriptor<T> {
    private final String tableName;
    private final Instantiator<T> instantiator;

    /*
     * only used to set ID on the pojo which is returned from here
     * enables us to set an exact value directly onto the primary key, i.e. int obtained as a result of an insert statement
     * */
    private final FieldValueSetter<T> primaryKeyFieldValueSetter;

    // converts only primary key into MySqlValue
    private final FieldMySqlValue primaryKeyFieldMySqlValue;

    // map of aliases pointing to each Fields' MySqlValue
    private final Map<String, FieldMySqlValue> aliasFieldMySqlValueMap = new HashMap<>();

    //todo obsolete
    private final Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //todo replace will list of ALL setters

    private final FilteredSelect filteredSelect;

    private final ResultSetToInstance<T> resultSetToInstance;
    private final MySqlValuesFilter<T> mySqlValuesFilter;

    private final InsertionExecutor<T> insertionExecutor;

    public MySQLModelDescriptor(MySQLServiceFieldsProvider mySQLServiceFieldsProvider, TypeLiteral<T> modelTypeLiteral) {
        Class<T> modelClass = (Class<T>) modelTypeLiteral.getRawType();
        tableName = mySQLServiceFieldsProvider.getTableName(modelClass);
        instantiator = mySQLServiceFieldsProvider.getInstantiator(modelClass);
        primaryKeyFieldMySqlValue = mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(modelClass);
        primaryKeyFieldValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(modelClass);
        aliasFieldMySqlValueMap.putAll(mySQLServiceFieldsProvider.getAliasFieldMySqlValues(modelClass));
        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(modelClass);
        filteredSelect = mySQLServiceFieldsProvider.getFilteredSelect(modelClass);
        resultSetToInstance = mySQLServiceFieldsProvider.getResultSetToInstance(modelClass);
        mySqlValuesFilter = mySQLServiceFieldsProvider.getMySqlValuesFilter(modelClass);

        Field primaryField = primaryKeyFieldValueSetter.getField();
        if (primaryField.getAnnotation(Nested.class) == null && !primaryField.getAnnotation(MySqlField.class).known()) {
            insertionExecutor = (queryBuilder, mySqlClient, toInsert) -> getPrimaryKeyFieldValueSetter().accept(toInsert, mySqlClient.executeInsertQuery(queryBuilder));
        } else {
            insertionExecutor = (queryBuilder, mySqlClient, toInsert) -> mySqlClient.executeInsertQueryWithKnownPrimaryKey(queryBuilder);
        }
    }

    ResultSetToInstance<T> getResultSetToInstance() {
        return resultSetToInstance;
    }

    Instantiator<T> getInstantiator() {
        return instantiator;
    }

    FieldMySqlValue getPrimaryKeyFieldMySqlValue() {
        return primaryKeyFieldMySqlValue;
    }

    Map<String, FieldMySqlValue> getAliasFieldMySqlValueMap() {
        return aliasFieldMySqlValueMap;
    }

    Map<String, FieldValueSetter<T>> getAllExceptPrimaryFieldValueSetterMap() {
        return allExceptPrimaryFieldValueSetterMap;
    }

    FieldValueSetter<T> getPrimaryKeyFieldValueSetter() {
        return primaryKeyFieldValueSetter;
    }

    String getTableName() {
        return tableName;
    }

    FilteredSelect getFilteredSelect() {
        return filteredSelect;
    }

    MySqlValuesFilter<T> getMySqlValuesFilter() {
        return mySqlValuesFilter;
    }

    public InsertionExecutor<T> insertExecutor() {
        return insertionExecutor;
    }
}
