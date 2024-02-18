package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

import java.util.List;
import java.util.Objects;

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

    public Student(Integer id) {
        this.id = id;
    }

    public Student(Integer id, String name, Type type, Diploma diploma, List<Course> courses) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.diploma = diploma;
        this.courses = courses;
    }

    public Student(String name, Type type, Diploma diploma, List<Course> courses) {
        this.name = name;
        this.type = type;
        this.diploma = diploma;
        this.courses = courses;
    }

    @Override
    public String toString() {
        String s = "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type;
        if (diploma != null) {
            s += ", diploma=" + diploma;
        } else {
            s += ", diploma=null";
        }
        return s + (", courses=" + courses +
                '}');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && Objects.equals(name, student.name) && Objects.equals(type, student.type) && Objects.equals(diploma.getId(), student.id) && Objects.equals(courses, student.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, diploma, courses);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Diploma getDiploma() {
        return diploma;
    }

    @Override
    public <D extends BaseDiploma> void setDiploma(D diploma) {
        this.diploma = (Diploma) diploma;
    }

    @Override
    public <C extends BaseCourse> void setCourses(List<C> courses) {
        this.courses = (List<Course>) courses;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
