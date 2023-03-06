package com.eu.atit.mysql.service.merging.direction;

import com.eu.atit.mysql.service.FieldValueGetter;
import com.eu.atit.mysql.service.FieldsMerger;

import java.util.List;

public  class SingleRoad extends Crossroads{
    public SingleRoad(FieldsMerger childMerger, FieldValueGetter nestedObjectGetter) {
        super(childMerger, nestedObjectGetter);
    }

    @Override
    public Object apply(List<Object> objects) {
        return childMerger.first(
                objects.stream().map(nestedObjectGetter::apply).toList()).get(0);
    }
}