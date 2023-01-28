package com.eu.atit.mysql.query;

import java.util.Objects;

import static com.eu.atit.mysql.query.SelectWithAliases.AS;

public class From extends KeyWord implements QueryPart {
    static final String FROM = System.lineSeparator() + "FROM\t";
    private final String tableName;
    private final String tableNameAlias;

    public From(String tableName) {
        this.tableName = tableName;
        this.tableNameAlias = tableName.toLowerCase();
    }

    @Override
    public String apply(String query) {
        return query.concat(FROM).concat(tableName).concat(AS).concat(tableNameAlias);
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

    @Override
    public String toString() {
        return "From{" +
                "tableName='" + tableName + '\'' +
                ", tableNameLowercase='" + tableNameAlias + '\'' +
                '}';
    }
}
