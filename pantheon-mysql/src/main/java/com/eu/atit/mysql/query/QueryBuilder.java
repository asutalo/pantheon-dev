package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class QueryBuilder {
    private static final String NONE = "";
    private static final String COMMA = ", ";
    private final List<QueryPart> queryParts = new LinkedList<>();
    private int paramIndex = 0;

    public void selectAll() {
        queryParts.add(new SelectAll());
    }


    public void select(Set<ColumnNameAndAlias> columnsAndAliases) {
        queryParts.add(new SelectWithAliases(columnsAndAliases));
    }

    public void insert(String tableName, LinkedList<MySqlValue> values) {
        injectIndexes(values);
        queryParts.add(new Insert(tableName, values));
    }

    public void delete() {
        queryParts.add(new Delete());
    }

    public void update(String tableName, LinkedList<MySqlValue> values) {
        injectIndexes(values);
        queryParts.add(new Update(tableName, values));
    }

    public void from(String tableName) {
        queryParts.add(new From(tableName));
    }

    public void where() {
        queryParts.add(new Where());
    }

    public void and() {
        queryParts.add(new And());
    }
    public void join(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        queryParts.add(new Join(targetTableName, targetId, sourceTableName, sourceId));
    }
    public void leftJoin(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        queryParts.add(new LeftJoin(targetTableName, targetId, sourceTableName, sourceId));
    }

    public void keyIsVal(MySqlValue value) {
        queryParts.add(new KeyVal(value.getMysqlType(), value.getKey(), value.getValue(), getSeparator(), getCurrentIndex()));
    }

    void setQueryParts(List<QueryPart> queryParts) {
        this.queryParts.clear();
        this.queryParts.addAll(queryParts);
    }

    private String getSeparator() {
        String separator = COMMA;

        if (queryParts.get(queryParts.size() - 1) instanceof KeyWord) {
            separator = NONE;
        }

        return separator;
    }

    String buildQueryString() {
        String query = "";
        for (QueryPart queryPart : queryParts) {
            query = queryPart.apply(query);
        }

        return query.concat(";");
    }

    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        String queryString = buildQueryString();

        PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);

        for (QueryPart queryPart : queryParts) {
            queryPart.apply(preparedStatement);
        }

        System.out.println("prepped: " + preparedStatement);
        return preparedStatement;
    }

    private int getCurrentIndex() {
        paramIndex++;
        return paramIndex;
    }

    private void injectIndexes(List<MySqlValue> values) {
        values.forEach(value -> value.setParamIndex(getCurrentIndex()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryBuilder that = (QueryBuilder) o;
        return paramIndex == that.paramIndex && Objects.equals(queryParts, that.queryParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParts, paramIndex);
    }
}
