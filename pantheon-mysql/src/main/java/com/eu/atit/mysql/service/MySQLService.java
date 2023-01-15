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
    private final MySqlClient mySqlClient;
    private final MySQLModelDescriptor<T> mySQLModelDescriptor;


    MySQLService(DataClient mySqlClient, MySQLModelDescriptor<T> mySQLModelDescriptor) {
        this.mySqlClient = (MySqlClient) mySqlClient;
        this.mySQLModelDescriptor = mySQLModelDescriptor;
    }

    @Override
    public void update(T toUpdate) throws SQLException {
        LinkedList<MySqlValue> mySqlValues = mySqlValues(toUpdate);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.update(mySQLModelDescriptor.getTableName(), mySqlValues);
        queryBuilder.where();
        queryBuilder.keyIsVal(mySQLModelDescriptor.getPrimaryKeyFieldMySqlValue().apply(toUpdate));

        if (mySqlClient.executeOtherDmlQuery(queryBuilder) <= 0) {
            throw new RuntimeException("Update failed");
        }
    }

    @Override
    public void save(T toSave) throws SQLException {
        LinkedList<MySqlValue> mySqlValues = mySqlValues(toSave);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert(mySQLModelDescriptor.getTableName(), mySqlValues);

        int insertId = mySqlClient.executeInsertQuery(queryBuilder);

        mySQLModelDescriptor.getPrimaryKeyFieldValueSetter().accept(toSave, insertId);
        System.out.println(toSave);
    }

    @Override
    public void delete(T toDelete) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from(mySQLModelDescriptor.getTableName());
        queryBuilder.where();
        queryBuilder.keyIsVal(mySQLModelDescriptor.getPrimaryKeyFieldMySqlValue().apply(toDelete));

        if (mySqlClient.executeOtherDmlQuery(queryBuilder) == 0) {
            throw new RuntimeException("Deletion failed");
        }
    }

    @Override
    public QueryBuilder filteredSelect() {

        //todo convert into 2 functions to avoid if check
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(mySQLModelDescriptor.getColumnsAndAliases());
        queryBuilder.from(mySQLModelDescriptor.getTableName());

        if(!mySQLModelDescriptor.getJoinInfos().isEmpty()){
            List<String> mixes = new ArrayList<>();
            for (JoinInfo joinInfo : mySQLModelDescriptor.getJoinInfos()) {

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

        Map<Object, List<T>> groupedByPrimaryKey = elements.stream().collect(Collectors.groupingBy(x -> {
            try {
                return mySQLModelDescriptor.getPrimaryKeyFieldValueSetter().getFieldValue(x);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }));

        List<T> joinedElements = new LinkedList<>();

        for (List<T> element : groupedByPrimaryKey.values()) {
            T original = element.get(0);

            for (T t : element) {
                for (SpecificFieldValueOverride<T> specificFieldValueOverride : mySQLModelDescriptor.getSpecificFieldValueOverrides()) {
                    specificFieldValueOverride.accept(original, t);
                }
            }

            joinedElements.add(original);
        }

        return joinedElements;
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
        T instance = mySQLModelDescriptor.getInstantiator().get();

        values.forEach((key, val) -> {
            if (mySQLModelDescriptor.getAllExceptPrimaryFieldValueSetterMap().containsKey(key)) {
                mySQLModelDescriptor.getAllExceptPrimaryFieldValueSetterMap().get(key).accept(instance, val);
            }
        });

        return instance;
    }

    private QueryBuilder filteredSelect(Map<String, Object> filter) {
        List<MySqlValue> filterMySqlValues = new ArrayList<>();
        filter.forEach((key, val) -> {
            if (mySQLModelDescriptor.getFieldMySqlValueMap().containsKey(key)) {
                filterMySqlValues.add(mySQLModelDescriptor.getFieldMySqlValueMap().get(key).of(val));
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
        T instance = mySQLModelDescriptor.getInstantiator().get();

        mySQLModelDescriptor.getSpecificFieldValueSetters().forEach(setter -> setter.accept(instance, row));

        if(!mySQLModelDescriptor.getSpecificNestedFieldValueSetters().isEmpty()){
            for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : mySQLModelDescriptor.getSpecificNestedFieldValueSetters()) {
                specificNestedFieldValueSetter.accept(instance, row, new ArrayList<>(List.of(mySQLModelDescriptor.getModelClass())));
            }
        }
        return instance;
    }

     T fullInstanceOfT(Map<String, Object> row, List<Class<?>> observedClasses) {
        T instance = mySQLModelDescriptor.getInstantiator().get();

        mySQLModelDescriptor.getSpecificFieldValueSetters().forEach(setter -> setter.accept(instance, row));

        if(!mySQLModelDescriptor.getSpecificNestedFieldValueSetters().isEmpty()){
            for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : mySQLModelDescriptor.getSpecificNestedFieldValueSetters()) {
                specificNestedFieldValueSetter.accept(instance, row, observedClasses);
            }
        }
        return instance;
    }

     T primaryInstanceOfT(Map<String, Object> row) {
        T instance = mySQLModelDescriptor.getInstantiator().get();

         mySQLModelDescriptor.getPrimaryKeyValueSetter().accept(instance, row);
        return instance;
    }

    private LinkedList<MySqlValue> mySqlValues(T user) {
        LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
        mySQLModelDescriptor.getNonPrimaryKeyFieldMySqlValues().forEach(getter -> mySqlValues.add(getter.apply(user)));
        return mySqlValues;
    }

    public MySQLModelDescriptor<T> getMySQLModelDescriptor() {
        return mySQLModelDescriptor;
    }
}
