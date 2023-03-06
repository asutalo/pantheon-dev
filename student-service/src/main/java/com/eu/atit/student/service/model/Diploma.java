package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;


public final class Diploma {
    @MySqlField(type = MysqlType.BOOLEAN)
    private Boolean obtained;
    @MySqlField(type = MysqlType.INT, primary = true, column = "id")
    @Nested(outward = true, eager = true)
    private Student student; //todo should not be in select statement as its ID is not stored anywhere in this object

    public Diploma() {
    }

    public Diploma(Student student, boolean obtained) {
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

    public Boolean getObtained() {
        return obtained;
    }

    public Student getStudent() {
        return student;
    }
}
