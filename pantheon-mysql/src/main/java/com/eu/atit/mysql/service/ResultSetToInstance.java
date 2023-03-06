package com.eu.atit.mysql.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ResultSetToInstance<T> {
    final MySQLModelDescriptor<T> mySQLModelDescriptor;

    ResultSetToInstance(MySQLModelDescriptor<T> mySQLModelDescriptor) {
        this.mySQLModelDescriptor = mySQLModelDescriptor;
    }

    public List<T> getAll(List<Map<String, Object>> resultSet) {
        List<T> elements = new LinkedList<>();
        for (Map<String, Object> row : resultSet) {
            elements.add(get(row));
        }

        return elements;
    }

    T get(Map<String, Object> row) {
        T instance = mySQLModelDescriptor.getInstantiator().get();

        mySQLModelDescriptor.getSpecificFieldValueSetters().forEach(setter -> setter.accept(instance, row));

        return instance;
    }

    T get(Map<String, Object> row, List<Class<?>> observedClasses) {
        T instance = mySQLModelDescriptor.getInstantiator().get();
        mySQLModelDescriptor.getSpecificFieldValueSetters().forEach(setter -> setter.accept(instance, row));

        return instance;
    }
}
