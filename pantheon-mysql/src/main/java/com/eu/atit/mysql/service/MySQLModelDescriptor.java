package com.eu.atit.mysql.service;

import com.google.inject.TypeLiteral;

import java.util.*;

public class MySQLModelDescriptor<T> {
    private final MySQLServiceFieldsProvider mySQLServiceFieldsProvider;
    private final Class<T> modelClass;

    private String tableName;
    private String tableNameLowercase;

    private Instantiator<T> instantiator;

    /*
     * only used to set ID on the pojo which is returned from here
     * enables us to set an exact value directly onto the primary key, i.e. int obtained as a result of an insert statement
     * */
    private FieldValueSetter<T> primaryKeyFieldValueSetter;
    /*
     * used to update the primary key based on a select statement (using a map from ResultSet and column aliases)
     * */
    private SpecificFieldValueSetter<T> primaryKeyValueSetter;
    private FieldValueGetter<T> primaryKeyFieldValueGetter;

    // converts only primary key into MySqlValue
    private FieldMySqlValue<T> primaryKeyFieldMySqlValue;

    private List<FieldMySqlValue<T>> nonPrimaryKeyFieldMySqlValues;

    // map of aliases pointing to each Fields' MySqlValue
    private final Map<String, FieldMySqlValue<T>> aliasFieldMySqlValueMap = new HashMap<>();

    /*
     * used to initialise a full POJO including primary key FROM select statement with table names and joins in mind
     * */
    private List<SpecificFieldValueSetter<T>> specificFieldValueSetters;
    private List<SpecificFieldValueOverride<T>> specificFieldValueOverrides;
    private List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters;
    private final Set<ColumnNameAndAlias> columnsAndAliases = new HashSet<>();
    private Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //no primary key included but will include not annotated fields as well

    //full traversal down all EAGER nested classes
    private List<JoinInfo> joinInfos;

    private FilteredSelect filteredSelect;

    private ResultSetToInstance<T> resultSetToInstance;

    public MySQLModelDescriptor(MySQLServiceFieldsProvider mySQLServiceFieldsProvider, TypeLiteral<T> modelTypeLiteral) {
        modelClass = (Class<T>) modelTypeLiteral.getRawType();
        this.mySQLServiceFieldsProvider = mySQLServiceFieldsProvider;
    }

    public void init() {
        tableName = mySQLServiceFieldsProvider.getTableName(modelClass);
        tableNameLowercase = mySQLServiceFieldsProvider.getTableNameLowercase(modelClass);
        instantiator = mySQLServiceFieldsProvider.getInstantiator(modelClass);
        nonPrimaryKeyFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(modelClass);
        primaryKeyFieldMySqlValue = mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(modelClass);
        primaryKeyFieldValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(modelClass);
        primaryKeyFieldValueGetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueGetter(modelClass);
        specificFieldValueSetters = mySQLServiceFieldsProvider.getSpecificFieldValueSetters(modelClass);
        specificFieldValueOverrides = mySQLServiceFieldsProvider.getSpecificFieldValueOverrides(modelClass);
        primaryKeyValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyValueSetter(modelClass);
        setAliasFieldMySqlValueMap();
        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(modelClass);

        specificNestedFieldValueSetters = mySQLServiceFieldsProvider.getSpecificNestedFieldValueSetters(modelClass);
        joinInfos = mySQLServiceFieldsProvider.getJoinInfos(modelClass);

        setColumnsAndAliases();
        setFilteredSelect();
        setResultSetToInstance();
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

    Class<T> getModelClass() {
        return modelClass;
    }

    Instantiator<T> getInstantiator() {
        return instantiator;
    }

    FieldMySqlValue<T> getPrimaryKeyFieldMySqlValue() {
        return primaryKeyFieldMySqlValue;
    }

    Map<String, FieldMySqlValue<T>> getAliasFieldMySqlValueMap() {
        return aliasFieldMySqlValueMap;
    }

    List<FieldMySqlValue<T>> getNonPrimaryKeyFieldMySqlValues() {
        return nonPrimaryKeyFieldMySqlValues;
    }

    List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters() {
        return specificFieldValueSetters;
    }

    List<SpecificFieldValueOverride<T>> getSpecificFieldValueOverrides() {
        return specificFieldValueOverrides;
    }

    SpecificFieldValueSetter<T> getPrimaryKeyValueSetter() {
        return primaryKeyValueSetter;
    }

    List<SpecificNestedFieldValueSetter<T>> getSpecificNestedFieldValueSetters() {
        return specificNestedFieldValueSetters;
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

    FieldValueGetter<T> getPrimaryKeyFieldValueGetter() {
        return primaryKeyFieldValueGetter;
    }

    String getTableName() {
        return tableName;
    }

    String getTableNameLowercase() {
        return tableNameLowercase;
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

    private void setResultSetToInstance() {
        boolean hasNestedList = joinInfos.stream().anyMatch(JoinInfo::isListJoin);

        if (hasNestedList) {
            resultSetToInstance = new ResultSetToInstanceWithListNesting<>(this);
        } else if (!specificNestedFieldValueSetters.isEmpty()) {
            resultSetToInstance = new ResultSetToInstanceWithNesting<>(this);
        } else {
            resultSetToInstance = new ResultSetToInstance<>(this);
        }
    }

    private void setFilteredSelect() {
        if (getJoinInfos().isEmpty()) {
            filteredSelect = new FilteredSelect(this);
        } else {
            filteredSelect = new JoinedFilterSelect(this);
        }
    }
}
