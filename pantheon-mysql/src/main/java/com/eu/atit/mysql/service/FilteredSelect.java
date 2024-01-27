package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.QueryBuilder;

import java.util.LinkedHashSet;

class FilteredSelect {

    private final QueryBuilder filteredSelectQueryBuilder;

    FilteredSelect(LinkedHashSet<ColumnNameAndAlias> columnsAndAliases, String tableName) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(columnsAndAliases);
        queryBuilder.from(tableName);

        filteredSelectQueryBuilder = queryBuilder;
    }

    QueryBuilder get() {
        return filteredSelectQueryBuilder;
    }
}
