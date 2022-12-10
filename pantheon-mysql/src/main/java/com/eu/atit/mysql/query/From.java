package com.eu.atit.mysql.query;

import java.util.Objects;

public class From extends KeyWord implements QueryPart {
    static final String FROM = System.lineSeparator() + "FROM ";
    static final String SPACE = " ";
    private final String tableName;
    private final String tableNameLowercase;

    public From(String tableName) {
        this.tableName = tableName;
        this.tableNameLowercase = tableName.toLowerCase();
    }

    @Override
    public String apply(String query) {
        return query.concat(FROM).concat(tableName).concat(SPACE).concat(tableNameLowercase);
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        From from = (From) o;
        return Objects.equals(tableName, from.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
