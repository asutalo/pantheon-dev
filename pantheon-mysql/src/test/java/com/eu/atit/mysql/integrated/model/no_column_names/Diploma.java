package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;


public final class Diploma implements BaseDiploma<Student> {
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

    Student getStudent() {
        return student;
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
    public<BS extends BaseStudent> void setStudent(BS student) {
        this.student = (Student) student;
    }

    @Override
    public Student getId() {
        return student;
    }
}
