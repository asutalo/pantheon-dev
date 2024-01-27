package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.mysql.cj.MysqlType;

import java.util.Objects;

@Table(name = "Course")
public class CourseCN implements BaseCourse {
    @MySqlField(type = MysqlType.INT, primary = true, column = "id")
    private Integer i;
    @MySqlField(type = MysqlType.VARCHAR, column = "name")
    private String n;

    public CourseCN() {
    }

    public CourseCN(String n) {
        this.n = n;
    }

    public CourseCN(Integer i, String n) {
        this.i = i;
        this.n = n;
    }

    @Override
    public String toString() {
        return "CourseCN[" +
                "i=" + i + ", " +
                "n=" + n + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseCN courseCN = (CourseCN) o;
        return Objects.equals(i, courseCN.i) && Objects.equals(n, courseCN.n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, n);
    }

    @Override
    public int getId() {
        return i;
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
