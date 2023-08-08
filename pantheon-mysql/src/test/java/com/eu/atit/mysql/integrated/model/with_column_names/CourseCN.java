package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.mysql.cj.MysqlType;
@Table(name="course")
public class CourseCN {
    @MySqlField(type = MysqlType.INT, primary = true, column = "id")
    private Integer i;
    @MySqlField(type = MysqlType.VARCHAR, column = "name")
    private String n;

    public CourseCN() {
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
}
