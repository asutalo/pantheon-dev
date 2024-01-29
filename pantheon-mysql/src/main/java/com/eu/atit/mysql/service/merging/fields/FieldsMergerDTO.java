package com.eu.atit.mysql.service.merging.fields;

import com.eu.atit.mysql.service.FieldValueSetter;
import com.eu.atit.mysql.service.merging.direction.Crossroads;

import java.util.List;
import java.util.function.Function;

public class FieldsMergerDTO {
    private final FieldValueSetter fieldValueSetter;
    private final Function<List<Object>, Object> crossroads;

    public FieldsMergerDTO(FieldValueSetter fieldValueSetter, Crossroads crossroads) {
        this.fieldValueSetter = fieldValueSetter;
        this.crossroads = crossroads;
    }

    public FieldValueSetter fieldValueSetter() {
        return fieldValueSetter;
    }


    public Object apply(List<Object> toMerge) {
        return crossroads.apply(toMerge);
    }
}
