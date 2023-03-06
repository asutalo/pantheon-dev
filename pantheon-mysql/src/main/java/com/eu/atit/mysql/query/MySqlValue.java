package com.eu.atit.mysql.query;

import com.mysql.cj.MysqlType;

import java.util.Objects;

public class MySqlValue {
    private final MysqlType mysqlType;
    private final String key;
    private final Object value;
    private int paramIndex;

    public MySqlValue(MysqlType mysqlType, String key, Object value) {
        this.mysqlType = mysqlType;
        this.key = key;
        this.value = value;
    }

    public MySqlValue(MysqlType mysqlType, String key, Object value, int paramIndex) {
        this.mysqlType = mysqlType;
        this.key = key;
        this.value = value;
        this.paramIndex = paramIndex;
    }

    public MysqlType getMysqlType() {
        return mysqlType;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    int getParamIndex() {
        return paramIndex;
    }

    void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MySqlValue that = (MySqlValue) o;

        if (paramIndex != that.paramIndex) return false;
        if (mysqlType != that.mysqlType) return false;
        if (!Objects.equals(key, that.key)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = mysqlType != null ? mysqlType.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + paramIndex;
        return result;
    }

    @Override
    public String toString() {
        return "MySqlValue{" +
               "mysqlType=" + mysqlType +
               ", key='" + key + '\'' +
               ", value=" + value +
               ", paramIndex=" + paramIndex +
               '}';
    }
}
