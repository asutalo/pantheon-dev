package com.eu.atit.mysql.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ResultSetToInstance<T> {
    private final Instantiator<T> instantiator;
    private final List<SpecificFieldValueSetter<T>> specificFieldValueSetters;

    ResultSetToInstance(Instantiator<T> instantiator, List<SpecificFieldValueSetter<T>> specificFieldValueSetters) {
        this.instantiator = instantiator;
        this.specificFieldValueSetters = specificFieldValueSetters;
    }

    public List<T> getAll(List<Map<String, Object>> resultSet) {
        List<T> elements = new LinkedList<>();
        for (Map<String, Object> row : resultSet) {
            elements.add(get(row));
        }

        return elements;
    }

    T get(Map<String, Object> row) {
        T instance = instantiator.get();

        specificFieldValueSetters.forEach(setter -> setter.accept(instance, row));

        return instance;
    }

    T getExact(Map<String, Object> t) {
        T instance = instantiator.get();
        specificFieldValueSetters.forEach(setter -> setter.acceptExact(instance, t));
        return instance;
    }

    T get(Map<String, Object> row, List<Class<?>> observedClasses) {
        T instance = instantiator.get();
        specificFieldValueSetters.forEach(setter -> setter.accept(instance, row));

        return instance;
    }
}
