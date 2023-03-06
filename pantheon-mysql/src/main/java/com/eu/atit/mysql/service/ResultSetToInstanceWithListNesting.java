package com.eu.atit.mysql.service;

import java.util.List;
import java.util.Map;

class ResultSetToInstanceWithListNesting<T> extends ResultSetToInstanceWithNesting<T> {
    ResultSetToInstanceWithListNesting(MySQLModelDescriptor<T> mySQLModelDescriptor) {
        super(mySQLModelDescriptor);
    }

    @Override
    public List<T> getAll(List<Map<String, Object>> resultSet) {
        List<T> elements = super.getAll(resultSet);
        return (List<T>) mySQLModelDescriptor.getFieldsMerger().first(elements);
    }
}
