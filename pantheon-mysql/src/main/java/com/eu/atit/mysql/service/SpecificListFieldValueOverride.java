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
    public void accept(T setFieldOn, T getValueFrom) {
        LinkedHashSet<T> valueToSet = new LinkedHashSet<>((List<T>) fieldValueGetter.apply(setFieldOn));

        System.out.println("base: " + valueToSet);
        List<T> apply = (List<T>) fieldValueGetter.apply(getValueFrom);

        System.out.println("adding: " + apply);
        valueToSet.addAll(apply);

        fieldValueSetter.accept(setFieldOn, new ArrayList<>(valueToSet));
    }
}
