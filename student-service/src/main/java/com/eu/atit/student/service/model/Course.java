package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

import java.util.Objects;

public  class Course {
    @MySqlField(type = MysqlType.INT, primary = true)
    private  Integer id;
    @MySqlField(type = MysqlType.VARCHAR)
    private  String name;

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Course() {}

    public Course(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Course[" +
                "id=" + id + ", " +
                "name=" + name + ']';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
