package com.eu.atit.mysql.test.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;


public final class Diploma {
    @MySqlField(type = MysqlType.BOOLEAN)
    private Boolean obtained;
    @MySqlField(type = MysqlType.INT, primary = true)
    @Nested(outward = true, eager = true)
    private Student student;

    public Diploma() {
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
}
