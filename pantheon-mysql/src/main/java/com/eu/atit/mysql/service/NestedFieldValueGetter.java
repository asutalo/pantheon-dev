package com.eu.atit.mysql.service;

import java.lang.reflect.Field;

class NestedFieldValueGetter extends FieldValueGetter {

    private final FieldValueGetter nestedPrimaryKeyFieldValueGetter;

    NestedFieldValueGetter(Field field, FieldValueGetter nestedPrimaryKeyFieldValueGetter) {
        super(field);
        this.nestedPrimaryKeyFieldValueGetter = nestedPrimaryKeyFieldValueGetter;
    }

    @Override
    public Object apply(Object t) {
        return nestedPrimaryKeyFieldValueGetter.apply(super.apply(t));
    }
}
