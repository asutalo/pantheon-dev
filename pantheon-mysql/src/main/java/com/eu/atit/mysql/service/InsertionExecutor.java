package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.QueryBuilder;

import java.sql.SQLException;

public interface InsertionExecutor<T> {
    void insert(QueryBuilder queryBuilder, MySqlClient mySqlClient, T toInsert) throws SQLException;
}
