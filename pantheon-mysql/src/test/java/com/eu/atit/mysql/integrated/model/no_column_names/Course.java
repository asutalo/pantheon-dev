package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

public class Course implements BaseCourse {
    @MySqlField(type = MysqlType.INT, primary = true)
    private Integer id;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

    public Course() {
    }

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
