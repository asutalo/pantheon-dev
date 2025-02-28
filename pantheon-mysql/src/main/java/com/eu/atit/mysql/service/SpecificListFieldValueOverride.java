package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//used when merging objects, specific to fetching an object that has a list of nested objects inside
class SpecificListFieldValueOverride<T> extends SpecificFieldValueOverride<T> {
    SpecificListFieldValueOverride(Field fieldToSet) {
        super(fieldToSet);
    }

    @Override
    public void accept(T targetObject, T sourceObject) {
        List<T> targetValue = asList(fieldAccessor.getValueFrom(targetObject));
        List<T> sourceValue = asList(fieldAccessor.getValueFrom(sourceObject));
        fieldAccessor.setValueOn(targetObject, new ArrayList<>(mergeValues(targetValue, sourceValue)));
    }

    private LinkedHashSet<T> mergeValues(List<T> targetValue, List<T> sourceValue) {
        LinkedHashSet<T> valueSet = new LinkedHashSet<>(targetValue);
        valueSet.addAll(sourceValue);
        return valueSet;
    }

    @SuppressWarnings("unchecked")
    private List<T> asList(Object value) {
        return (List<T>) value;
    }
}
