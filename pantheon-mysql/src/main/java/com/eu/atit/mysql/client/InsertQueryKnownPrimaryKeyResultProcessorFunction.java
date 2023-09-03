package com.eu.atit.mysql.client;

import java.sql.PreparedStatement;

class InsertQueryKnownPrimaryKeyResultProcessorFunction extends InsertQueryResultProcessorFunction {

    @Override
    public Integer apply(PreparedStatement preparedStatement) {
        return execute(preparedStatement, executedStatement -> 1);
    }
}