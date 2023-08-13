package com.eu.atit.mysql.integrated.model.base;

public interface BaseDiploma {
    Boolean obtained();

    <BS extends BaseStudent> void setStudent(BS student);
}
