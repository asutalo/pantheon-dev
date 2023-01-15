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

        Map<Object, List<T>> groupedByPrimaryKey = elements.stream().collect(Collectors.groupingBy(x -> mySQLModelDescriptor.getPrimaryKeyFieldValueGetter().apply(x)));

        List<T> joinedElements = new LinkedList<>();

        for (List<T> element : groupedByPrimaryKey.values()) {
            T original = element.get(0);

            for (T t : element) {
                for (SpecificFieldValueOverride<T> specificFieldValueOverride : mySQLModelDescriptor.getSpecificFieldValueOverrides()) {
                    specificFieldValueOverride.accept(original, t);
                }
            }

            joinedElements.add(original);
        }

        return joinedElements;
    }
}
