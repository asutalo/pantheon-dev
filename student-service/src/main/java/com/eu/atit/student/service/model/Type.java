package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

public class Type {
    @MySqlField(type = MysqlType.INT, primary = true, column = "typeID")
    private Integer idTTTTT;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

    public Type(String name) {
        this.name = name;
    }

    //mandatory empty constructor
    private Type(){}

    @Override
    public String toString() {
        return "Type{" +
                "id=" + idTTTTT +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer id() {
        return idTTTTT;
    }

    public void setName(String name) {
        this.name = name;
    }
}
