package com.eu.atit.mysql.client;

import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.pantheon.client.data.DataClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MySqlClient implements DataClient {
    private final Connector connector;
    private final SelectQueryResultProcessor selectQueryResultProcessor;
    private final InsertQueryResultProcessorFunction insertQueryResultProcessorFunction;
    private final InsertQueryKnownPrimaryKeyResultProcessorFunction insertQueryKnownPrimaryKeyResultProcessorFunction;
    private final OtherDmlQueryResultProcessorFunction otherDmlQueryResultProcessorFunction;

    public MySqlClient(Connector connector) {
        this.connector = connector;

        selectQueryResultProcessor = new SelectQueryResultProcessor();
        insertQueryResultProcessorFunction = new InsertQueryResultProcessorFunction();
        insertQueryKnownPrimaryKeyResultProcessorFunction = new InsertQueryKnownPrimaryKeyResultProcessorFunction();
        otherDmlQueryResultProcessorFunction = new OtherDmlQueryResultProcessorFunction();

    }

    public List<Map<String, Object>> executeSelectQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, selectQueryResultProcessor);
    }

    public int executeInsertQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, insertQueryResultProcessorFunction);
    }

    public int executeInsertQueryWithKnownPrimaryKey(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, insertQueryKnownPrimaryKeyResultProcessorFunction);
    }

    public int executeOtherDmlQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, otherDmlQueryResultProcessorFunction);
    }

    public void executeSql(String[] queries) throws SQLException {
        Connection connection = connector.connect();
        for (String sql : queries) {
            connection.prepareStatement(sql).execute();
        }

        connection.close();
    }

    <T> T execute(QueryBuilder queryBuilder, Function<PreparedStatement, T> preparedStatementExecutor) throws SQLException {
        Connection connection = connector.connect();
        T queryResults = execute(queryBuilder, preparedStatementExecutor, connection);
        connector.close(connection);
        return queryResults;
    }

    <T> T execute(QueryBuilder queryBuilder, Function<PreparedStatement, T> preparedStatementExecutor, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = queryBuilder.prepareStatement(connection);
        T queryResults = preparedStatementExecutor.apply(preparedStatement);
        preparedStatement.close();
        return queryResults;
    }

    public Connection startTransaction() throws SQLException {
        Connection connection = connector.connect();
        connection.setAutoCommit(false);
        return connection;
    }

    public void endTransaction(Connection connection) throws SQLException {
        connection.commit();
        connector.close(connection);
    }

    public void rollbackTransaction(Connection connection) throws SQLException {
        connection.rollback();
    }

    public int executeInsertQueryWithKnownPrimaryKey(QueryBuilder queryBuilder, Connection connection) throws SQLException {
        return execute(queryBuilder, insertQueryKnownPrimaryKeyResultProcessorFunction, connection);
    }

    public int executeInsertQuery(QueryBuilder queryBuilder, Connection connection) throws SQLException {
        return execute(queryBuilder, insertQueryResultProcessorFunction, connection);
    }

    public int executeOtherDmlQuery(QueryBuilder queryBuilder, Connection connection) throws SQLException {
        return execute(queryBuilder, otherDmlQueryResultProcessorFunction, connection);
    }
}
