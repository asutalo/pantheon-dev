package com.eu.atit.mysql.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FieldsMerger {
    final FieldValueGetter primaryFieldValueGetter;

    /*
    * list of fields that should be merged and instructions on how to merge
    *
    * */
    private final List<FieldsMergerDTO> fieldsMergerDTOList;

    public FieldsMerger(FieldValueGetter primaryFieldValueGetter, List<FieldsMergerDTO> fieldsMergerDTOList) {
        this.primaryFieldValueGetter = primaryFieldValueGetter;
        this.fieldsMergerDTOList = fieldsMergerDTOList;
    }

    public List<?> first(List<?> toMerge) {
        Map<Object, List<Object>> groupedById = new LinkedHashMap<>();

        for (Object t : toMerge) {
            groupedById.computeIfAbsent(primaryFieldValueGetter.apply(t), k -> new ArrayList<>()).add(t);
        }

        for (Map.Entry<Object, List<Object>> groupedAs : groupedById.entrySet()) {
            Object groupedA = second(groupedAs.getValue());
            groupedById.put(groupedAs.getKey(), List.of(groupedA));
        }
        return groupedById.values().stream().flatMap(Collection::stream).toList();
    }

    Object second(List<Object> toMerge) {
        Object originalA = toMerge.get(0);
        for (FieldsMergerDTO tFieldsMergerDTO : fieldsMergerDTOList) {
            tFieldsMergerDTO.fieldValueSetter().accept(originalA, tFieldsMergerDTO.apply(toMerge));
        }

        return originalA;
    }

    static class DeadEnd extends FieldsMerger {
        public DeadEnd(FieldValueGetter primaryFieldValueGetter, List<FieldsMergerDTO> fieldsMergerDTOList) {
            super(primaryFieldValueGetter, fieldsMergerDTOList);
        }

        @Override
        Object second(List<Object> toMerge) {
            return toMerge.get(0);
        }
    }
}
