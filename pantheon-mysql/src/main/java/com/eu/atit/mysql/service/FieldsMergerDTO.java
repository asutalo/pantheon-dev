package com.eu.atit.mysql.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldsMergerDTO {
    private final FieldValueSetter<Object> fieldValueSetter;
    private final Function<List<Object>, Object> crossroads;

    abstract static class Crossroads implements Function<List<Object>, Object>{
         final FieldsMerger childMerger;
         final FieldValueGetter nestedObjectGetter;

        public Crossroads(FieldsMerger childMerger, FieldValueGetter nestedObjectGetter){
            this.childMerger = childMerger;
            this.nestedObjectGetter = nestedObjectGetter;
        }
    }

    static class ListRoad extends Crossroads{
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

    static class SingleRoad extends Crossroads{
        public SingleRoad(FieldsMerger childMerger, FieldValueGetter nestedObjectGetter) {
            super(childMerger, nestedObjectGetter);
        }

        @Override
        public Object apply(List<Object> objects) {
            return childMerger.first(
                    objects.stream().map(nestedObjectGetter::apply).toList()).get(0);
        }
    }

    FieldsMergerDTO(FieldValueSetter<Object> fieldValueSetter, Crossroads crossroads) {
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
