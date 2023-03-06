package com.eu.atit.mysql.service.merging.direction;

import com.eu.atit.mysql.service.FieldValueGetter;
import com.eu.atit.mysql.service.merging.fields.FieldsMerger;
import com.eu.atit.mysql.service.merging.fields.FieldsMergerDTO;

import java.util.List;

public class DeadEnd extends FieldsMerger {
    public DeadEnd(FieldValueGetter primaryFieldValueGetter, List<FieldsMergerDTO> fieldsMergerDTOList) {
        super(primaryFieldValueGetter, fieldsMergerDTOList);
    }

    @Override
    public Object second(List<Object> toMerge) {
        return toMerge.get(0);
    }
}