package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
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

    public StudentCN(Integer i) {
        this.i = i;
    }

    public StudentCN(Integer i, String n, TypeCN t, DiplomaCN diplomaCN, List<CourseCN> courses) {
        this.i = i;
        this.n = n;
        this.t = t;
        this.diplomaCN = diplomaCN;
        this.courses = courses;
    }

    public StudentCN(String n, TypeCN t, DiplomaCN diplomaCN, List<CourseCN> courses) {
        this.n = n;
        this.t = t;
        this.diplomaCN = diplomaCN;
        this.courses = courses;
    }

    @Override
    public String toString() {
        String s = "StudentCN{" +
                "i=" + i +
                ", n='" + n + '\'' +
                ", t=" + t;

        if (diplomaCN != null) {
            s += ", diplomaCN=" + diplomaCN;
        } else {
            s += ", diplomaCN=null";
        }

        return s +
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
    public <D extends BaseDiploma> void setDiploma(D diploma) {
        this.diplomaCN = (DiplomaCN) diploma;
    }

    @Override
    public <C extends BaseCourse> void setCourses(List<C> courses) {
        this.courses = (List<CourseCN>) courses;
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
