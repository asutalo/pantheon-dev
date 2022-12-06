package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

public final class Student {
    @MySqlField(type = MysqlType.INT, primary = true)
    private int id;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

    @Nested(outward = true, link = "type_id")
    private Type type;
//    private Diploma diploma;
//    private List<Course> courses;

//    public Student(int id, String name, Type type, Diploma diploma, List<Course> courses) {
//        this.id = id;
//        this.name = name;
//        this.type = type;
//        this.diploma = diploma;
//        this.courses = courses;
//    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
