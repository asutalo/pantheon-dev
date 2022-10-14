package com.eu.atit.mysql.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryPart {
    default String apply(String query) {
        return query;
    }

    default void apply(PreparedStatement preparedStatement) throws SQLException {
    }
}
