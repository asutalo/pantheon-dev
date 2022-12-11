package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

import java.util.Objects;

public final class Diploma {
    @MySqlField(type = MysqlType.INT, primary = true)

    private int id;
    @MySqlField(type = MysqlType.BOOLEAN)

    private boolean obtained;

    @Nested(outward = true)
    private Student student;

    @Override
    public String toString() {
        return "Diploma{" +
                "id=" + id +
                ", obtained=" + obtained +
                '}';
    }
}
