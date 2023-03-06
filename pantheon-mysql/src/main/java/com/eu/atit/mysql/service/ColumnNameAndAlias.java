package com.eu.atit.mysql.service;

import java.util.Objects;

public class ColumnNameAndAlias {
    private final String columnName;
    private final String alias;

    public ColumnNameAndAlias(String columnName, String alias) {
        this.columnName = columnName;
        this.alias = alias;
    }

    public String fieldName() {
        return columnName;
    }

    public String alias() {
        return alias;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ColumnNameAndAlias) obj;
        return Objects.equals(this.columnName, that.columnName) &&
               Objects.equals(this.alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, alias);
    }

    @Override
    public String toString() {
        return "ColumnNameAndAlias[" +
               "columnName=" + columnName + ", " +
               "alias=" + alias + ']';
    }

}
