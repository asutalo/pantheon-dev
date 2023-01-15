package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.QueryBuilder;

class FilteredSelect {
    final MySQLModelDescriptor<?> mySQLModelDescriptor;

    private final QueryBuilder filteredSelectQueryBuilder;

    FilteredSelect(MySQLModelDescriptor<?> mySQLModelDescriptor) {
        this.mySQLModelDescriptor = mySQLModelDescriptor;

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(mySQLModelDescriptor.getColumnsAndAliases());
        queryBuilder.from(mySQLModelDescriptor.getTableName());

        filteredSelectQueryBuilder = queryBuilder;
    }

    QueryBuilder get() {
        return filteredSelectQueryBuilder;
    }
}
