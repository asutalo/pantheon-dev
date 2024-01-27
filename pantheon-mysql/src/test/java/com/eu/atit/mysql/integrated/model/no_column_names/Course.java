package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

import java.util.Objects;

public class Course implements BaseCourse {
    @MySqlField(type = MysqlType.INT, primary = true)
    private Integer id;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

    public Course() {
    }

    public Course(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
