package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.mysql.cj.MysqlType;

@Table(name = "Student_Course")
public class StudentCourseCN {
    @MySqlField(type = MysqlType.INT, column = "student_id")
    private Integer s;

    @MySqlField(type = MysqlType.INT, primary = true, known = true, column = "course_id")
    private Integer c;

    public StudentCourseCN() {
    }

    public StudentCourseCN(int s, int c) {
        this.s = s;
        this.c = c;
    }
}
