package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

import java.util.List;
import java.util.Objects;

@Table(name = "Student")
public class StudentCN implements BaseStudent {
    @MySqlField(type = MysqlType.INT, primary = true, column = "id")
    private int i;
    @MySqlField(type = MysqlType.VARCHAR, column = "name")
    private String n;

    @MySqlField(type = MysqlType.INT, column = "type_id")
    @Nested(outward = true, eager = true)
    private TypeCN t;

    @Nested(inward = true, eager = true)
    private DiplomaCN diplomaCN;

    @Nested(eager = true)
    private List<CourseCN> courses;

    public StudentCN() {
    }

    public StudentCN(String n, TypeCN t, DiplomaCN diplomaCN, List<CourseCN> cours) {
        this.n = n;
        this.t = t;
        this.diplomaCN = diplomaCN;
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "StudentCN{" +
                "i=" + i +
                ", n='" + n + '\'' +
                ", t=" + t +
                ", diploma=" + diplomaCN.getS().i +
                ", courses=" + courses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCN studentCN = (StudentCN) o;
        return i == studentCN.i && Objects.equals(n, studentCN.n) && Objects.equals(t, studentCN.t) && Objects.equals(diplomaCN, studentCN.diplomaCN) && Objects.equals(courses, studentCN.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, n, t, diplomaCN, courses);
    }

    @Override
    public int getId() {
        return i;
    }

    @Override
    public DiplomaCN getDiploma() {
        return diplomaCN;
    }

    @Override
    public String getName() {
        return n;
    }

    @Override
    public void setName(String name) {
        n = name;
    }
}
