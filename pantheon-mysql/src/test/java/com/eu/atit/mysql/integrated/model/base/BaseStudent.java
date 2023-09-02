package com.eu.atit.mysql.integrated.model.base;

public interface BaseStudent extends WithId {
    BaseDiploma getDiploma();

    void setName(String name);

    String getName();
}
