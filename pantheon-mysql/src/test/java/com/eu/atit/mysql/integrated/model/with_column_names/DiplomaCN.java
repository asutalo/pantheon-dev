package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.mysql.cj.MysqlType;

import java.util.Objects;

@Table(name = "Diploma")
public final class DiplomaCN implements BaseDiploma {
    @MySqlField(type = MysqlType.BOOLEAN, column = "obtained")
    private Boolean o;
    @MySqlField(type = MysqlType.INT, primary = true)
    @Nested(outward = true, eager = true)
    private StudentCN s;

    public DiplomaCN() {
    }

    public DiplomaCN(StudentCN student, Boolean obtained) {
        s = student;
        o = obtained;
    }

    @Override
    public String toString() {
        return "DiplomaCN{" +
                "o=" + o +
                ", s.id=" + s.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        DiplomaCN diplomaCN = (DiplomaCN) o1;
        return Objects.equals(o, diplomaCN.o) && Objects.equals(s.getId(), diplomaCN.s.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(o, s);
    }

    StudentCN getS() {
        return s;
    }

    @Override
    public Boolean obtained() {
        return o;
    }

    @Override
    public void setObtained(Boolean obtained) {
        o = obtained;
    }

    @Override
    public <BS extends BaseStudent> void setStudent(BS student) {
        s = (StudentCN) student;
    }

    @Override
    public BaseStudent getStudent() {
        return s;
    }

    @Override
    public int getId() {
        return s.getId();
    }
}
