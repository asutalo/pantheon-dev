package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

import java.util.List;

public class Student implements BaseStudent {
    @MySqlField(type = MysqlType.INT, primary = true)
    private int id;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

    @MySqlField(type = MysqlType.INT)
    @Nested(outward = true, eager = true)
    private Type type;

    @Nested(inward = true, eager = true)
    private Diploma diploma;

    @Nested(eager = true)
    private List<Course> courses;

    public Student() {
    }

    public Student(String name, Type type, Diploma diploma, List<Course> courses) {
        this.name = name;
        this.type = type;
        this.diploma = diploma;
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "Student{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", type=" + type +
               ", diploma=" + diploma.getStudent().id +
               ", courses=" + courses +
               '}';
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Diploma getDiploma() {
        return diploma;
    }
}
