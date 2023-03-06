package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.pantheon.helper.Pair;
import com.google.inject.TypeLiteral;

import java.util.*;

public class MySQLModelDescriptor<T> {
    private final MySQLServiceFieldsProvider mySQLServiceFieldsProvider;
    private final Class<T> modelClass;
    private String tableName;
    private String tableNameLowercase;

    private FieldsMerger fieldsMerger;

    public FieldsMerger getFieldsMerger() {
        return fieldsMerger;
    }

    private boolean hasDescendantWithList;
    public boolean isHasDescendantWithList() {
        return hasDescendantWithList;
    }

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
    private FieldValueGetter primaryKeyFieldValueGetter;

    // converts only primary key into MySqlValue
    private FieldMySqlValue primaryKeyFieldMySqlValue;

    private List<FieldMySqlValue> nonPrimaryKeyFieldMySqlValues;

    // map of aliases pointing to each Fields' MySqlValue
    private final Map<String, FieldMySqlValue> aliasFieldMySqlValueMap = new HashMap<>();

    /*
     * used to initialise a full POJO including primary key FROM select statement with table names and joins in mind
     * */
    private List<SpecificFieldValueSetter<T>> specificFieldValueSetters;
    private List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters;
    private final Set<ColumnNameAndAlias> columnsAndAliases = new HashSet<>();
    private Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //no primary key included but will include not annotated fields as well

    //full traversal down all EAGER nested classes
    private List<JoinInfo> joinInfos;

    private FilteredSelect filteredSelect;

    private ResultSetToInstance<T> resultSetToInstance;
    private MySqlValuesFilter<T> mySqlValuesFilter;

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

        setAliasFieldMySqlValueMap();
        primaryKeyValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyValueSetter(modelClass);
        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(modelClass);

        specificNestedFieldValueSetters = mySQLServiceFieldsProvider.getSpecificNestedFieldValueSetters(modelClass);
        joinInfos = mySQLServiceFieldsProvider.getJoinInfos(modelClass);

        setColumnsAndAliases();
        setFilteredSelect();
        setResultSetToInstance();
        setMySqlValuesFilter();


        FieldValueGetter nestedPrimaryKeyValueGetter = mySQLServiceFieldsProvider.getNestedPrimaryKeyFieldValueGetter(modelClass);

        hasDescendantWithList = joinInfos.stream().anyMatch(JoinInfo::hasAnyList);
        if(mySQLServiceFieldsProvider.getSpecificListFieldValueOverrides(modelClass).size() > 0 || hasDescendantWithList) {
            fieldsMerger = new FieldsMerger(nestedPrimaryKeyValueGetter != null ? nestedPrimaryKeyValueGetter : primaryKeyFieldValueGetter, mySQLServiceFieldsProvider.myNestedModelsDTOs(modelClass));
        } else {
            fieldsMerger = new FieldsMerger.DeadEnd(nestedPrimaryKeyValueGetter != null ? nestedPrimaryKeyValueGetter : primaryKeyFieldValueGetter, mySQLServiceFieldsProvider.myNestedModelsDTOs(modelClass));
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

    Class<T> getModelClass() {
        return modelClass;
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

    List<FieldMySqlValue> getNonPrimaryKeyFieldMySqlValues() {
        return nonPrimaryKeyFieldMySqlValues;
    }

    List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters() {
        return specificFieldValueSetters;
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

    FieldValueGetter getPrimaryKeyFieldValueGetter() {
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
    private void setMySqlValuesFilter(){
        if(mySQLServiceFieldsProvider.getNestedFieldsMySqlValue(modelClass).isEmpty()){
            mySqlValuesFilter = new NonPrimaryMySqlValuesFilter<>(this);
        } else {
            mySqlValuesFilter = new MySqlValuesFilterWithNestedPrimaryKey<>(this);
        }
    }

    static class MySqlValuesFilterWithNestedPrimaryKey<T> extends NonPrimaryMySqlValuesFilter<T>{
        private final List<Pair<FieldMySqlValue, FieldValueGetter>> nestedFieldMySqlValues;

        MySqlValuesFilterWithNestedPrimaryKey(MySQLModelDescriptor<T> mySQLModelDescriptor) {
            super(mySQLModelDescriptor);
            this.nestedFieldMySqlValues = mySQLModelDescriptor.getNestedPrimaryFieldMySqlValues();
        }

        @Override
        LinkedList<MySqlValue> get(T object) {
            LinkedList<MySqlValue> mySqlValues = super.get(object);
            nestedFieldMySqlValues.forEach(nestedFieldMySqlValue -> mySqlValues.add(nestedFieldMySqlValue.left().apply(nestedFieldMySqlValue.right().apply(object))));
            return mySqlValues;
        }
    }

    List<Pair<FieldMySqlValue, FieldValueGetter>> getNestedPrimaryFieldMySqlValues() {
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

    static abstract class MySqlValuesFilter<T>{
        final MySQLModelDescriptor<T> mySQLModelDescriptor;

        MySqlValuesFilter(MySQLModelDescriptor<T> mySQLModelDescriptor) {
            this.mySQLModelDescriptor = mySQLModelDescriptor;
        }

        abstract LinkedList<MySqlValue> get(T object);
    }

    static class NonPrimaryMySqlValuesFilter<T> extends MySqlValuesFilter<T>{
        NonPrimaryMySqlValuesFilter(MySQLModelDescriptor<T> mySQLModelDescriptor) {
            super(mySQLModelDescriptor);
        }

        @Override
        LinkedList<MySqlValue> get(T object) {
            LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
            mySQLModelDescriptor.getNonPrimaryKeyFieldMySqlValues().forEach(getter -> mySqlValues.add(getter.apply(object)));

            return mySqlValues;
        }
    }
}
