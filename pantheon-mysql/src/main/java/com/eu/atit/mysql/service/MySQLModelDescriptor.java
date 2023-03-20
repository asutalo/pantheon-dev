package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.filter.MySqlValuesFilter;
import com.eu.atit.mysql.service.filter.MySqlValuesFilterWithNestedPrimaryKey;
import com.eu.atit.mysql.service.filter.NonPrimaryMySqlValuesFilter;
import com.eu.atit.pantheon.helper.Pair;
import com.google.inject.TypeLiteral;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLModelDescriptor<T> {
    private final MySQLServiceFieldsProvider mySQLServiceFieldsProvider;
    private final Class<T> modelClass;
    private final String tableName; 
    private final String tableNameLowercase;

    private final Instantiator<T> instantiator;

    /*
     * only used to set ID on the pojo which is returned from here
     * enables us to set an exact value directly onto the primary key, i.e. int obtained as a result of an insert statement
     * */
    private final FieldValueSetter<T> primaryKeyFieldValueSetter;

    // converts only primary key into MySqlValue
    private final FieldMySqlValue primaryKeyFieldMySqlValue;

    private final List<FieldMySqlValue> nonPrimaryKeyFieldMySqlValues;

    // map of aliases pointing to each Fields' MySqlValue
    private final Map<String, FieldMySqlValue> aliasFieldMySqlValueMap = new HashMap<>();

    /*
     * used to initialise a full POJO including primary key FROM select statement with table names and joins in mind
     * */
    private final List<SpecificFieldValueSetter<T>> specificFieldValueSetters;
    private final Set<ColumnNameAndAlias> columnsAndAliases = new HashSet<>();
    
    private final Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //no primary key included but will include not annotated fields as well

    //full traversal down all EAGER nested classes
    private final List<JoinInfo> joinInfos;

    private FilteredSelect filteredSelect;

    private ResultSetToInstance<T> resultSetToInstance;
    private MySqlValuesFilter<T> mySqlValuesFilter;

    public MySQLModelDescriptor(MySQLServiceFieldsProvider mySQLServiceFieldsProvider, TypeLiteral<T> modelTypeLiteral) {
        modelClass = (Class<T>) modelTypeLiteral.getRawType();
        this.mySQLServiceFieldsProvider = mySQLServiceFieldsProvider;

        tableName = mySQLServiceFieldsProvider.getTableName(modelClass);
        tableNameLowercase = mySQLServiceFieldsProvider.getTableNameLowercase(modelClass);
        instantiator = mySQLServiceFieldsProvider.getInstantiator(modelClass);
        nonPrimaryKeyFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(modelClass);
        primaryKeyFieldMySqlValue = mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(modelClass);

        primaryKeyFieldValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(modelClass);
        specificFieldValueSetters = mySQLServiceFieldsProvider.getSpecificFieldValueSetters(modelClass);

        setAliasFieldMySqlValueMap();
        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(modelClass);

        joinInfos = mySQLServiceFieldsProvider.getJoinInfos(modelClass);

        setColumnsAndAliases();
        setFilteredSelect();
        resultSetToInstance = mySQLServiceFieldsProvider.getResultSetToInstance(modelClass);
        resultSetToInstance = mySQLServiceFieldsProvider.getResultSetToInstance(modelClass);
        setMySqlValuesFilter();
    }

    private void setMySqlValuesFilter(){
        if(mySQLServiceFieldsProvider.getNestedFieldsMySqlValue(modelClass).isEmpty()){
            mySqlValuesFilter = new NonPrimaryMySqlValuesFilter<>(this);
        } else {
            mySqlValuesFilter = new MySqlValuesFilterWithNestedPrimaryKey<>(this);
        }
    }

    private void setColumnsAndAliases() {
        for (SpecificFieldValueSetter<T> specificFieldValueSetter : specificFieldValueSetters) {
            columnsAndAliases.add(specificFieldValueSetter.fieldNameAndAlias(tableNameLowercase));
        }

        for (JoinInfo joinInfo : joinInfos) {
            columnsAndAliases.addAll(joinInfo.fieldNameAndAliases());
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

    public List<FieldMySqlValue> getNonPrimaryKeyFieldMySqlValues() {
        return nonPrimaryKeyFieldMySqlValues;
    }

    Set<ColumnNameAndAlias> getColumnsAndAliases() {
        return columnsAndAliases;
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

    List<JoinInfo> getJoinInfos() {
        return joinInfos;
    }

    FilteredSelect getFilteredSelect() {
        return filteredSelect;
    }

    private void setAliasFieldMySqlValueMap() {
        aliasFieldMySqlValueMap.put(primaryKeyFieldMySqlValue.alias(), primaryKeyFieldMySqlValue);
        nonPrimaryKeyFieldMySqlValues.forEach(fieldMySqlValue -> aliasFieldMySqlValueMap.put(fieldMySqlValue.alias(), fieldMySqlValue));
    }

    public List<Pair<FieldMySqlValue, FieldValueGetter>> getNestedPrimaryFieldMySqlValues() {
        return mySQLServiceFieldsProvider.getNestedFieldsMySqlValue(modelClass);
    }

    private void setFilteredSelect() {
        if (getJoinInfos().isEmpty()) {
            filteredSelect = new FilteredSelect(this);
        } else {
            filteredSelect = new JoinedFilterSelect(this);
        }
    }

    MySqlValuesFilter<T> getMySqlValuesFilter() {
        return mySqlValuesFilter;
    }
}
