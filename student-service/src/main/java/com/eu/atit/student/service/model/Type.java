package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

public class Type {
    @MySqlField(type = MysqlType.INT, primary = true)
    private Integer id;
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
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer id() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
