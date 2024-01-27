package com.eu.atit.mysql.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ResultSetToInstanceWithNesting<T> extends ResultSetToInstance<T> {
    private final List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters;
    private final Class<T> modelClass;

    ResultSetToInstanceWithNesting(Instantiator<T> instantiator, List<SpecificFieldValueSetter<T>> specificFieldValueSetters, List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters, Class<T> modelClass) {
        super(instantiator, specificFieldValueSetters);
        this.specificNestedFieldValueSetters = specificNestedFieldValueSetters;
        this.modelClass = modelClass;
    }

    @Override
    T get(Map<String, Object> row) {
        T instance = super.get(row);

        for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : specificNestedFieldValueSetters) {
            specificNestedFieldValueSetter.accept(instance, row, new ArrayList<>(List.of(modelClass)));
        }

        return instance;
    }

    @Override
    T get(Map<String, Object> row, List<Class<?>> observedClasses) {
        T instance = super.get(row, observedClasses);

        for (SpecificNestedFieldValueSetter<T> specificNestedFieldValueSetter : specificNestedFieldValueSetters) {
            specificNestedFieldValueSetter.accept(instance, row, observedClasses);
        }

        return instance;
    }

    @Override
    public String toString() {
        return "ResultSetToInstanceWithNesting{}";
    }
}
