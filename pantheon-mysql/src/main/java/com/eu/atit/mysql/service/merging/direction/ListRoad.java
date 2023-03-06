package com.eu.atit.mysql.service.merging.direction;

import com.eu.atit.mysql.service.FieldValueGetter;
import com.eu.atit.mysql.service.FieldsMerger;

import java.util.List;

public  class ListRoad extends Crossroads{
    public ListRoad(FieldsMerger childMerger, FieldValueGetter nestedObjectGetter) {
        super(childMerger, nestedObjectGetter);
    }

    @Override
    public Object apply(List<Object> objects) {
        List<Object> toMerge = objects.stream().flatMap(x -> {
            List<Object> apply = (List<Object>) nestedObjectGetter.apply(x);
            return apply.stream();
        }).toList();
        return childMerger.first(toMerge);
    }
}
