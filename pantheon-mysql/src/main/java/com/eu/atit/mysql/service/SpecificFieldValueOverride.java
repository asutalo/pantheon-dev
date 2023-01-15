package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiConsumer;

//used when merging objects, specific to fetching an object that has a list of nested objects inside
class SpecificFieldValueOverride<T> implements BiConsumer<T, T> {
    private final FieldValueSetter<T> fieldValueSetter;
    private final FieldValueGetter<T> fieldValueGetter;

    private final boolean isList;

    SpecificFieldValueOverride(Field fieldToSet) {
        fieldValueGetter = new FieldValueGetter<>(fieldToSet);
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        Type genericType = fieldToSet.getGenericType();
        isList = genericType.getTypeName().contains("List");
    }

    @Override
    public void accept(T setFieldOn, T getValueFrom) {
        if(!isList){
            fieldValueSetter.accept(setFieldOn, fieldValueGetter.apply(getValueFrom));
        } else {
            List<T> originalList = (List<T>) fieldValueGetter.apply(setFieldOn);
            List<T> additionalList = (List<T>) fieldValueGetter.apply(getValueFrom);
            originalList.addAll(additionalList);
            fieldValueSetter.accept(setFieldOn, originalList);
        }
    }
}
