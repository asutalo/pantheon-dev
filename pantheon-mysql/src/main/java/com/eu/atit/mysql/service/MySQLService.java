package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.pantheon.client.data.DataClient;
import com.eu.atit.pantheon.helper.Pair;
import com.eu.atit.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLService<T> implements DataService<T, QueryBuilder> {
    private final Class<T> servingType;
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
    private final MySqlClient mySqlClient;
    /*
    * only used to set ID on the pojo which is returned from here
    * */
    private FieldValueSetter<T> primaryKeyFieldValueSetter;
    private String tableName;

    //full traversal down all EAGER nested classes
    private List<JoinInfo> joinInfos;

    MySQLService(DataClient mySqlClient, TypeLiteral<T> typeLiteral) {
        this.mySqlClient = (MySqlClient) mySqlClient;
        this.servingType = (Class<T>) typeLiteral.getType();
    }

    SpecificFieldValueSetter<T> getPrimaryKeyValueSetter() {
        return primaryKeyValueSetter;
    }

    @Override
    public void update(T toUpdate) throws SQLException {
        LinkedList<MySqlValue> mySqlValues = mySqlValues(toUpdate);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.update(tableName, mySqlValues);
        queryBuilder.where();
        queryBuilder.keyIsVal(primaryKeyFieldMySqlValue.apply(toUpdate));

        if (mySqlClient.executeOtherDmlQuery(queryBuilder) <= 0) {
            throw new RuntimeException("Update failed");
        }
    }

    @Override
    public void save(T toSave) throws SQLException {
        LinkedList<MySqlValue> mySqlValues = mySqlValues(toSave);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert(tableName, mySqlValues);

        int insertId = mySqlClient.executeInsertQuery(queryBuilder);

        primaryKeyFieldValueSetter.accept(toSave, insertId);
        System.out.println(toSave);
    }

    @Override
    public void delete(T toDelete) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from(tableName);
        queryBuilder.where();
        queryBuilder.keyIsVal(primaryKeyFieldMySqlValue.apply(toDelete));

        if (mySqlClient.executeOtherDmlQuery(queryBuilder) == 0) {
            throw new RuntimeException("Deletion failed");
        }
    }

    @Override
    public QueryBuilder filteredSelect() {

        //todo convert into 2 functions to avoid if check
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(columnsAndAliases);
        queryBuilder.from(tableName);

        if(!joinInfos.isEmpty()){
            List<String> mixes = new ArrayList<>();
            for (JoinInfo joinInfo : joinInfos) {

                String x = joinInfo.targetTableLowercase().concat(".".concat(joinInfo.targetId())) + " = " + joinInfo.sourceTableName().concat(".").concat(joinInfo.sourceId());
                String y = joinInfo.sourceTableName().concat(".").concat(joinInfo.sourceId()) + " = " + joinInfo.targetTableLowercase().concat(".".concat(joinInfo.targetId()));

                if (!mixes.contains(x) && !mixes.contains(y)) {
                    mixes.add(x);
                    mixes.add(y);
                    queryBuilder.join(joinInfo.targetTableName(), joinInfo.targetId(), joinInfo.sourceTableName(), joinInfo.sourceId());
                }
            }
        }

        return queryBuilder;
    }

    public QueryBuilder filteredSelectFunctional() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(columnsAndAliases);
        queryBuilder.from(tableName);

        if(!joinInfos.isEmpty()){
            List<String> mixes = new ArrayList<>();
            for (JoinInfo joinInfo : joinInfos) {

                String x = joinInfo.targetTableLowercase().concat(".".concat(joinInfo.targetId())) + " = " + joinInfo.sourceTableName().concat(".").concat(joinInfo.sourceId());
                String y = joinInfo.sourceTableName().concat(".").concat(joinInfo.sourceId()) + " = " + joinInfo.targetTableLowercase().concat(".".concat(joinInfo.targetId()));

                if (!mixes.contains(x) && !mixes.contains(y)) {
                    mixes.add(x);
                    mixes.add(y);
                    queryBuilder.join(joinInfo.targetTableName(), joinInfo.targetId(), joinInfo.sourceTableName(), joinInfo.sourceId());
                }
            }
        }

        return queryBuilder;
    }

    @Override
    public T get(QueryBuilder filteredSelect) throws SQLException, IllegalStateException {
        List<Map<String, Object>> resultSet = mySqlClient.executeSelectQuery(filteredSelect);

        if (resultSet.size() == 1) {
            return fullInstanceOfT(resultSet.get(0));
        }

        throw new IllegalStateException();
    }

    @Override
    public T get(Map<String, Object> filter) throws SQLException, IllegalStateException {
        QueryBuilder queryBuilder = filteredSelect(filter);

        return get(queryBuilder);
    }

    @Override
    public List<T> getAll() throws SQLException {
        return getAll(filteredSelect());
    }

    @Override
    public List<T> getAll(QueryBuilder filteredSelect) throws SQLException {
        List<Map<String, Object>> resultSet = mySqlClient.executeSelectQuery(filteredSelect);
        List<T> elements = new LinkedList<>();

        for (Map<String, Object> row : resultSet) {
            elements.add(fullInstanceOfT(row));
        }

        Map<Object, List<T>> collect = elements.stream().collect(Collectors.groupingBy(x -> {
            try {
                return primaryKeyFieldValueSetter.getField().get(x);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }));

        List<T> elements2 = new LinkedList<>();


        for (List<T> ts : collect.values()) {
            T original = ts.get(0);

            for (T t : ts) {
                for (SpecificFieldValueOverride<T> specificFieldValueOverride : specificFieldValueOverrides) {
                    specificFieldValueOverride.accept(original, t);
                }
            }

            elements2.add(original);
        }

        return elements2;
    }

    @Override
    public List<T> getAll(Map<String, Object> filter) throws SQLException, IllegalStateException {
        QueryBuilder queryBuilder = filteredSelect(filter);

        return getAll(queryBuilder);
    }

    /*
    * Used to initialise a pojo that will be inserted/updated
    * Does not include primary keys to avoid nulling it out
    * */
    @Override
    public T instanceOfT(Map<String, Object> values) {
        T instance = instantiator.get();

        values.forEach((key, val) -> {
            if (allExceptPrimaryFieldValueSetterMap.containsKey(key)) {
                allExceptPrimaryFieldValueSetterMap.get(key).accept(instance, val);
            }
        });

        return instance;
    }

    private QueryBuilder filteredSelect(Map<String, Object> filter) {
        List<MySqlValue> filterMySqlValues = new ArrayList<>();
        filter.forEach((key, val) -> {
            if (fieldMySqlValueMap.containsKey(key)) {
                filterMySqlValues.add(fieldMySqlValueMap.get(key).of(val));
            }
        });

        if (filterMySqlValues.isEmpty()) throw new IllegalStateException("Provided filters would produce no results");

        QueryBuilder queryBuilder = filteredSelect();
        queryBuilder.where();
        Iterator<MySqlValue> iterator = filterMySqlValues.iterator();

        while (iterator.hasNext()) {
            queryBuilder.keyIsVal(iterator.next());
            if (iterator.hasNext()) queryBuilder.and();
        }
        return queryBuilder;
    }

     T fullInstanceOfT(Map<String, Object> row) {
        T instance = instantiator.get();

        specificFieldValueSetters.forEach(setter -> setter.accept(instance, row));

        if(!specificNestedFieldValueSetters.isEmpty()){
            for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : specificNestedFieldValueSetters) {
                specificNestedFieldValueSetter.accept(instance, row, new ArrayList<>(List.of(servingType)));
            }
        }
        return instance;
    }

     T fullInstanceOfT(Map<String, Object> row, List<Class<?>> observedClasses) {
        T instance = instantiator.get();

        specificFieldValueSetters.forEach(setter -> setter.accept(instance, row));

        if(!specificNestedFieldValueSetters.isEmpty()){
            for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : specificNestedFieldValueSetters) {
                specificNestedFieldValueSetter.accept(instance, row, observedClasses);
            }
        }
        return instance;
    }

     T primaryInstanceOfT(Map<String, Object> row) {
        T instance = instantiator.get();

         primaryKeyValueSetter.accept(instance, row);
        return instance;
    }

    Map<String, FieldMySqlValue<T>> getFieldMySqlValueMap() {
        return fieldMySqlValueMap;
    }

    private LinkedList<MySqlValue> mySqlValues(T user) {
        LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
        nonPrimaryKeyFieldMySqlValues.forEach(getter -> mySqlValues.add(getter.apply(user)));
        return mySqlValues;
    }

    void init(MySQLServiceFieldsProvider mySQLServiceFieldsProvider) {
        mySQLServiceFieldsProvider.validateClass(servingType);
        tableName = mySQLServiceFieldsProvider.getTableName(servingType);
        instantiator = mySQLServiceFieldsProvider.getInstantiator(servingType);
        nonPrimaryKeyFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(servingType);
        primaryKeyFieldMySqlValue = mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(servingType);
        primaryKeyFieldValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(servingType);
        specificFieldValueSetters = mySQLServiceFieldsProvider.getSpecificFieldValueSetters(servingType);
        specificFieldValueOverrides = mySQLServiceFieldsProvider.getSpecificFieldValueOverrides(servingType);
        primaryKeyValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyValueSetter(servingType);
        primaryKeyValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyValueSetter(servingType);
        specificNestedFieldValueSetters = mySQLServiceFieldsProvider.getSpecificNestedFieldValueSetters(servingType);
        joinInfos = mySQLServiceFieldsProvider.getJoinInfos(servingType);
        columnsAndAliases = mySQLServiceFieldsProvider.getColumnsAndAliases(tableName.toLowerCase(), specificFieldValueSetters, joinInfos);

        fieldMySqlValueMap.put(primaryKeyFieldMySqlValue.alias(), primaryKeyFieldMySqlValue);
        nonPrimaryKeyFieldMySqlValues.forEach(fieldMySqlValue -> fieldMySqlValueMap.put(fieldMySqlValue.alias(), fieldMySqlValue));

        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(servingType);
    }

    FieldMySqlValue<T> getPrimaryKeyFieldMySqlValue() {
        return primaryKeyFieldMySqlValue;
    }

    List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters() {
        return specificFieldValueSetters;
    }

    String getTableName() {
        return tableName;
    }

    ArrayList<ColumnNameAndAlias> columnsAndAliases() {
        return columnsAndAliases;
    }

    List<JoinInfo> getJoinInfos() {
        return joinInfos;
    }

    Class<T> getServingType() {
        return servingType;
    }
}
