package com.eu.atit.mysql.service.merging.direction;

import com.eu.atit.mysql.service.FieldValueGetter;
import com.eu.atit.mysql.service.merging.fields.FieldsMerger;

import java.util.List;
import java.util.function.Function;

public abstract class Crossroads implements Function<List<Object>, Object> {
    final FieldsMerger childMerger;
    final FieldValueGetter nestedObjectGetter;

    public Crossroads(FieldsMerger childMerger, FieldValueGetter nestedObjectGetter){
        this.childMerger = childMerger;
        this.nestedObjectGetter = nestedObjectGetter;
    }
}
