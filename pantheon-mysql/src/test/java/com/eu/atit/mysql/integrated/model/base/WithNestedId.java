package com.eu.atit.mysql.integrated.model.base;

public interface WithNestedId<Y extends WithId> extends WithId{
    int getId();
}
