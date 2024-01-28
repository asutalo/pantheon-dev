package com.eu.atit.mysql.integrated.model.no_column_names;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.mysql.cj.MysqlType;

@Table(name = "Student_Course")
public class StudentCourse {
    @MySqlField(type = MysqlType.INT)
    private Integer student_id;

    @MySqlField(type = MysqlType.INT, primary = true, known = true)
    private Integer course_id;

    public StudentCourse() {
    }

    public StudentCourse(int s, int c) {
        this.student_id = s;
        this.course_id = c;
    }
}
