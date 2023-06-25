package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;
import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {
    private static final String NONE = "";
    private static final String COMMA = ", ";
    private final List<QueryPart> queryParts = new ArrayList<>();
    private int paramIndex = 0;

    static final String QUERY_END = ";";

    public void selectAll() {
        queryParts.add(new SelectAll());
    }

    public void select(LinkedHashSet<ColumnNameAndAlias> columnsAndAliases) {
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

    private String getSeparator() {
        String separator = COMMA;

        if (queryParts.get(queryParts.size() - 1) instanceof KeyWord) {
            separator = NONE;
        }

        return separator;
    }

    public String buildQueryString() {
        String query = "";
        for (QueryPart queryPart : queryParts) {
            query = queryPart.apply(query);
        }

        return query.concat(QUERY_END);
    }

    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        String queryString = buildQueryString();

        PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);

        for (QueryPart queryPart : queryParts) {
            queryPart.apply(preparedStatement);
        }

        System.out.println(preparedStatement);

        return preparedStatement;
    }

    public List<QueryPart> getQueryParts() {
        return queryParts;
    }


    void addQueryParts(List<QueryPart> queryParts) {
        this.queryParts.addAll(queryParts);
    }
    public List<KeyVal> getKeyValues() {
        return queryParts.stream().filter(queryPart -> queryPart instanceof KeyVal).map(queryPart -> (KeyVal) queryPart).collect(Collectors.toList());
    }

    private int getCurrentIndex() {
        paramIndex++;
        return paramIndex;
    }

    private void injectIndexes(List<MySqlValue> values) {
        values.forEach(value -> value.setParamIndex(getCurrentIndex()));
    }

    @IgnoreCoverage
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryBuilder that = (QueryBuilder) o;

        if (paramIndex != that.paramIndex) return false;
        return queryParts.equals(that.queryParts);
    }

    @IgnoreCoverage
    @Override
    public int hashCode() {
        int result = queryParts.hashCode();
        result = 31 * result + paramIndex;
        return result;
    }

    @IgnoreCoverage
    @Override
    public String toString() {
        return "QueryBuilder{" +
               "queryParts=" + queryParts +
               ", paramIndex=" + paramIndex +
               '}';
    }
}
