package com.eu.atit.mysql.test.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

public class Course {
    @MySqlField(type = MysqlType.INT, primary = true)
    private Integer id;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

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
}
