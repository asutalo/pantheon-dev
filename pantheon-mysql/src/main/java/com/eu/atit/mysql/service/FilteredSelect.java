package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.QueryBuilder;

import java.util.LinkedHashSet;

class FilteredSelect {

    private final LinkedHashSet<ColumnNameAndAlias> columnsAndAliases;
    private final String tableName;

    FilteredSelect(LinkedHashSet<ColumnNameAndAlias> columnsAndAliases, String tableName) {
        this.columnsAndAliases = columnsAndAliases;
        this.tableName = tableName;
    }

    QueryBuilder get() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(columnsAndAliases);
        queryBuilder.from(tableName);

        return queryBuilder;
    }
}
