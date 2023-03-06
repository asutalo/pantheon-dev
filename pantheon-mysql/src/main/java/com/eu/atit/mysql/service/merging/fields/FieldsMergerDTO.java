package com.eu.atit.mysql.service.merging.fields;

import java.util.List;
import java.util.function.Function;

import com.eu.atit.mysql.service.FieldValueSetter;
import com.eu.atit.mysql.service.merging.direction.Crossroads;

public class FieldsMergerDTO {
    private final FieldValueSetter<Object> fieldValueSetter;
    private final Function<List<Object>, Object> crossroads;

    public FieldsMergerDTO(FieldValueSetter<Object> fieldValueSetter, Crossroads crossroads) {
        this.fieldValueSetter = fieldValueSetter;
        this.crossroads = crossroads;
    }

    public FieldValueSetter<Object> fieldValueSetter() {
        return fieldValueSetter;
    }


    public Object apply(List<Object> toMerge) {
        return crossroads.apply(toMerge);
    }
}
