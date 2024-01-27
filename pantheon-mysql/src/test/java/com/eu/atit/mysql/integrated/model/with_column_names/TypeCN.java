package com.eu.atit.mysql.integrated.model.with_column_names;

import com.eu.atit.mysql.integrated.model.base.BaseType;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.mysql.cj.MysqlType;

import java.util.Objects;

@Table(name="Type")
public class TypeCN implements BaseType {
    @MySqlField(type = MysqlType.INT, primary = true, column = "id")
    private Integer i;
    @MySqlField(type = MysqlType.VARCHAR, column = "name")
    private String n;

    public TypeCN() {
    }

    public TypeCN(int i, String n) {
        this.i = i;
        this.n = n;
    }

    public TypeCN(String n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "TypeCN{" +
               "i=" + i +
               ", n='" + n + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TypeCN typeCN = (TypeCN) o;

        if (!Objects.equals(i, typeCN.i)) return false;
        return Objects.equals(n, typeCN.n);
    }

    @Override
    public int hashCode() {
        int result = i != null ? i.hashCode() : 0;
        result = 31 * result + (n != null ? n.hashCode() : 0);
        return result;
    }

    public int getI() {
        return i;
    }

    @Override
    public int getId() {
        return i;
    }

    @Override
    public void setName(String name) {
        n = name;
    }

    @Override
    public String getName() {
        return n;
    }
}
