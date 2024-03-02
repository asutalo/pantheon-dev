package com.eu.atit.mysql.integrated.model.base;

public interface BaseDiploma extends WithNestedId {
    Boolean obtained();

    void setObtained(Boolean obtained);

    BaseStudent getStudent();

    <BS extends BaseStudent> void setStudent(BS student);
}
