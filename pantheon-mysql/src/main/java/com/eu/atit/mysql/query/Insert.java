package com.eu.atit.mysql.query;

import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Insert implements QueryPart {
    static final String DELIMITER = ", ";
    static final String PLACEHOLDER = "?";
    static final String INSERT = "INSERT INTO\t";
    static final String COLUMNS_START = System.lineSeparator().concat("\t\t(");
    static final String COLUMNS_END = ")".concat(System.lineSeparator());
    static final String VALUES_START = "VALUES".concat(System.lineSeparator()).concat("\t\t(");
    static final String VALUES_END = ")";
    private static final String valuesDecorator = COLUMNS_END.concat(VALUES_START);
    private final List<MySqlValue> valuesForQuery;
    private final List<MySqlValue> valuesForParams;
    private final String insertIntoDecorator;
    ;

    public Insert(String tableName, LinkedList<MySqlValue> valuesForQuery) {
        this.valuesForQuery = new LinkedList<>(valuesForQuery);
        this.valuesForParams = new LinkedList<>(valuesForQuery);

        this.insertIntoDecorator = INSERT.concat(tableName);
    }

    @Override
    public String apply(String query) {
        List<MySqlValue> values = new ArrayList<>(valuesForQuery); //todo LOL fix this
        MySqlValue mySqlValue = values.get(0);
        String mySqlValueKey = mySqlValue.getKey();

        StringBuilder keysBuilder = new StringBuilder(mySqlValueKey);
        StringBuilder placeholdersBuilder = new StringBuilder(PLACEHOLDER);

        values.remove(0);

        for (MySqlValue value : values) {
            keysBuilder.append(DELIMITER);
            placeholdersBuilder.append(DELIMITER);

            keysBuilder.append(value.getKey());
            placeholdersBuilder.append(PLACEHOLDER);
        }

        return query.concat(insertIntoDecorator).concat(COLUMNS_START).concat(keysBuilder.toString()).concat(valuesDecorator).concat(placeholdersBuilder.toString()).concat(VALUES_END);
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        for (MySqlValue mySqlValue : valuesForParams) {
            preparedStatement.setObject(mySqlValue.getParamIndex(), mySqlValue.getValue(), mySqlValue.getMysqlType());
        }
    }

    @IgnoreCoverage
    public List<MySqlValue> getValuesForParams() {
        return valuesForParams;
    }

    @IgnoreCoverage
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Insert insert = (Insert) o;

        if (!Objects.equals(valuesForQuery, insert.valuesForQuery))
            return false;
        if (!Objects.equals(valuesForParams, insert.valuesForParams))
            return false;
        if (!Objects.equals(insertIntoDecorator, insert.insertIntoDecorator))
            return false;
        return Objects.equals(valuesDecorator, insert.valuesDecorator);
    }

    @IgnoreCoverage
    @Override
    public int hashCode() {
        int result = valuesForQuery != null ? valuesForQuery.hashCode() : 0;
        result = 31 * result + (valuesForParams != null ? valuesForParams.hashCode() : 0);
        result = 31 * result + (insertIntoDecorator != null ? insertIntoDecorator.hashCode() : 0);
        result = 31 * result + (valuesDecorator != null ? valuesDecorator.hashCode() : 0);
        return result;
    }


    @IgnoreCoverage
    @Override
    public String toString() {
        return "Insert{" +
                "valuesForQuery=" + valuesForQuery +
                ", valuesForParams=" + valuesForParams +
                ", insertIntoDecorator='" + insertIntoDecorator + '\'' +
                ", valuesDecorator='" + valuesDecorator + '\'' +
                '}';
    }
}
