package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

import java.util.Objects;


public final class Diploma implements BaseDiploma {
    @MySqlField(type = MysqlType.BOOLEAN)
    private Boolean obtained;
    @MySqlField(type = MysqlType.INT, primary = true)
    @Nested(outward = true, eager = true)
    private Student student;

    public Diploma() {
    }

    public Diploma(Student student, Boolean obtained) {
        this.student = student;
        this.obtained = obtained;
    }

    @Override
    public String toString() {
        return "Diploma{" +
                "obtained=" + obtained +
                ", student=" + student +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diploma diploma = (Diploma) o;
        return Objects.equals(obtained, diploma.obtained) && Objects.equals(student, diploma.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obtained, student);
    }

    @Override
    public Student getStudent() {
        return student;
    }

    @Override
    public <BS extends BaseStudent> void setStudent(BS student) {
        this.student = (Student) student;
    }

    @Override
    public Boolean obtained() {
        return obtained;
    }

    @Override
    public void setObtained(Boolean obtained) {
        this.obtained = obtained;
    }

    @Override
    public int getId() {
        return student.getId();
    }
}
