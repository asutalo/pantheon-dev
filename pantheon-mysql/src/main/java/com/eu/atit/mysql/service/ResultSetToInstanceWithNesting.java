package com.eu.atit.mysql.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ResultSetToInstanceWithNesting<T> extends ResultSetToInstance<T> {
    ResultSetToInstanceWithNesting(MySQLModelDescriptor<T> mySQLModelDescriptor) {
        super(mySQLModelDescriptor);
    }

    @Override
    T get(Map<String, Object> row) {
        T instance = super.get(row);

        for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : mySQLModelDescriptor.getSpecificNestedFieldValueSetters()) {
            specificNestedFieldValueSetter.accept(instance, row, new ArrayList<>(List.of(mySQLModelDescriptor.getModelClass())));
        }

        return instance;
    }

    @Override
    T get(Map<String, Object> row, List<Class<?>> observedClasses) {
        T instance = super.get(row, observedClasses);

        for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : mySQLModelDescriptor.getSpecificNestedFieldValueSetters()) {
            specificNestedFieldValueSetter.accept(instance, row, observedClasses);
        }

        return instance;
    }
}
