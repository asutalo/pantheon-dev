package com.eu.atit.mysql.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
