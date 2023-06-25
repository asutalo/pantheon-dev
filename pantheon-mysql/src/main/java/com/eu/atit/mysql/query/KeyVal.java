package com.eu.atit.mysql.query;

import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;
import com.mysql.cj.MysqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class KeyVal implements QueryPart {
    static final String IS_VAL = " = ?";
    private final Object value;
    private final int index;
    private final MysqlType targetType;

    private final String keyValDecorator;

    public KeyVal(MysqlType targetType, String key, Object value, String separator, int index) {
        this.targetType = targetType;
        this.value = value;
        this.index = index;

        this.keyValDecorator = separator.concat(key).concat(IS_VAL);
    }

    @Override
    public String apply(String query) {
        return query.concat(keyValDecorator);
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setObject(index, value, targetType);
    }

    @IgnoreCoverage
    public Object getValue() {
        return value;
    }

    @IgnoreCoverage
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final KeyVal keyVal = (KeyVal) o;

        if (index != keyVal.index) return false;
        if (!Objects.equals(value, keyVal.value)) return false;
        if (targetType != keyVal.targetType) return false;
        return Objects.equals(keyValDecorator, keyVal.keyValDecorator);
    }
    @IgnoreCoverage
    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + index;
        result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
        result = 31 * result + (keyValDecorator != null ? keyValDecorator.hashCode() : 0);
        return result;
    }

    @IgnoreCoverage
    @Override
    public String toString() {
        return "KeyVal{" +
               "value=" + value +
               ", index=" + index +
               ", targetType=" + targetType +
               ", keyValDecorator='" + keyValDecorator + '\'' +
               '}';
    }
}
