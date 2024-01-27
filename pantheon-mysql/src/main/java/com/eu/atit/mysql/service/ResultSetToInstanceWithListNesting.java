package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.merging.fields.FieldsMerger;

import java.util.List;
import java.util.Map;

class ResultSetToInstanceWithListNesting<T> extends ResultSetToInstanceWithNesting<T> {
    private final FieldsMerger fieldsMerger;

    ResultSetToInstanceWithListNesting(Instantiator<T> instantiator, List<SpecificFieldValueSetter<T>> specificFieldValueSetters, List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters, Class<T> modelClass, FieldsMerger fieldsMerger) {
        super(instantiator, specificFieldValueSetters, specificNestedFieldValueSetters, modelClass);
        this.fieldsMerger = fieldsMerger;
    }

    @Override
    public List<T> getAll(List<Map<String, Object>> resultSet) {
        List<T> elements = super.getAll(resultSet);
        return (List<T>) fieldsMerger.first(elements);
    }
}
