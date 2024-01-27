package com.eu.atit.mysql.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Function;

class InsertQueryResultProcessorFunction implements Function<PreparedStatement, Integer> {
    @Override
    public Integer apply(PreparedStatement preparedStatement) {
        return execute(preparedStatement, executedStatement -> {
            ResultSet generatedKeys = executedStatement.getGeneratedKeys();
            generatedKeys.next();

            return generatedKeys.getInt(Statement.RETURN_GENERATED_KEYS);
        });
    }

    Integer execute(PreparedStatement preparedStatement, ResultProcessor processor) {
        try {
            if (preparedStatement.executeUpdate() > 0) {
                return processor.process(preparedStatement);
            } else {
                throw new RuntimeException("Insert failed, no rows inserted");
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    interface ResultProcessor {
        Integer process(PreparedStatement preparedStatement) throws SQLException;
    }
}