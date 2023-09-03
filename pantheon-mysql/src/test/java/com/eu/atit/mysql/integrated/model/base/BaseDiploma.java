package com.eu.atit.mysql.integrated.model.base;

public interface BaseDiploma<Y> extends WithNestedId<Y> {
    Boolean obtained();

    void setObtained(Boolean obtained);

    <BS extends BaseStudent> void setStudent(BS student);
}
