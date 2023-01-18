package com.eu.atit.student.service.model;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

import java.util.Objects;

public  class Course {
    @MySqlField(type = MysqlType.INT, primary = true, column = "CCCCOUNT")
    private  Integer idCCCC;
    @MySqlField(type = MysqlType.VARCHAR)
    private  String name;

    public int id() {
        return idCCCC;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Course) obj;
        return this.idCCCC == that.idCCCC &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCCCC, name);
    }

    @Override
    public String toString() {
        return "Course[" +
                "id=" + idCCCC + ", " +
                "name=" + name + ']';
    }

}
