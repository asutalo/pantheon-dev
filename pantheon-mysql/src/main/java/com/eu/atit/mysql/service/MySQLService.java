package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.pantheon.client.data.DataClient;
import com.eu.atit.pantheon.service.data.DataService;

import java.sql.SQLException;
import java.util.*;

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

        mySQLModelDescriptor.insertExecutor().insert(queryBuilder, mySqlClient, toSave);
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
        return mySQLModelDescriptor.getFilteredSelect().get();
    }

    @Override
    public T get(QueryBuilder filteredSelect) throws SQLException, IllegalStateException {
        List<Map<String, Object>> resultSet = mySqlClient.executeSelectQuery(filteredSelect);

        if (resultSet.size() == 1) {
            return fullInstanceOfT(resultSet.get(0));
        }

        throw new IllegalStateException("No elements found");
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
        return mySQLModelDescriptor.getResultSetToInstance().getAll(resultSet);
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
        return fullInstanceOfT(values);
//        //todo does not support nesting atm
//        T instance = mySQLModelDescriptor.getInstantiator().get();
//
//        values.forEach((key, val) -> {
//            FieldValueSetter fieldValueSetter = mySQLModelDescriptor.getAllExceptPrimaryFieldValueSetterMap().get(key);
//            if (fieldValueSetter != null) {
//                fieldValueSetter.accept(instance, val);
//            }
//        });
//
//        return instance;
    }

    private QueryBuilder filteredSelect(Map<String, Object> filter) {
        List<MySqlValue> filterMySqlValues = new ArrayList<>();

        filter.forEach((key, val) -> {
            FieldMySqlValue fieldMySqlValue = mySQLModelDescriptor.getAliasFieldMySqlValueMap().get(key); // todo mySQLModelDescriptor.getAliasFieldMySqlValueMap() as global variable in this class?
            if (fieldMySqlValue != null) {
                //todo if val == List add multiple filters chained with OR
                filterMySqlValues.add(fieldMySqlValue.of(val));
            }
        });

        if (filterMySqlValues.isEmpty()) throw new IllegalStateException("Provided filters do not match any attribute");

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
        return mySQLModelDescriptor.getResultSetToInstance().get(row);
    }

    public String getTableName() {
        return mySQLModelDescriptor.getTableName();
    }

    private LinkedList<MySqlValue> mySqlValues(T user) {
        return mySQLModelDescriptor.getMySqlValuesFilter().get(user);
    }
}
