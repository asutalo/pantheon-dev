package com.eu.atit.mysql.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Update implements QueryPart {
    static final String UPDATE = "UPDATE ";
    private static final String DELIMITER = ", ";
    private final String tableName;
    private final List<MySqlValue> valuesForQuery;
    private final List<MySqlValue> valuesForParams;
    private static final String PLACEHOLDER = " = ?";


    public Update(String tableName, LinkedList<MySqlValue> values) {
        this.tableName = tableName;
        this.valuesForQuery = new LinkedList<>(values);
        this.valuesForParams = new LinkedList<>(values);
    }

    @Override
    public String apply(String query) {
        List<MySqlValue> values = new ArrayList<>(valuesForQuery); //todo LOL fix this
        MySqlValue mySqlValue = values.get(0);

        StringBuilder keysPlaceholderBuilder = new StringBuilder(mySqlValue.getKey().concat(PLACEHOLDER));

        values.remove(0);

        for (MySqlValue value : values) {
            keysPlaceholderBuilder.append(DELIMITER);
            keysPlaceholderBuilder.append(value.getKey().concat(" = ?"));
        }

        return query.concat(UPDATE).concat("\t").concat(tableName).concat(System.lineSeparator()).concat("SET\t\t").concat(keysPlaceholderBuilder.toString());
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        for (MySqlValue mySqlValue : valuesForParams) {
            preparedStatement.setObject(mySqlValue.getParamIndex(), mySqlValue.getValue(), mySqlValue.getMysqlType());
        }
    }


    public List<MySqlValue> getValuesForParams() {
        return valuesForParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Update update = (Update) o;
        return Objects.equals(tableName, update.tableName) && Objects.equals(valuesForQuery, update.valuesForQuery) && Objects.equals(valuesForParams, update.valuesForParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, valuesForQuery, valuesForParams);
    }

    @Override
    public String toString() {
        return "Update{" +
               "tableName='" + tableName + '\'' +
               ", valuesForQuery=" + valuesForQuery +
               ", valuesForParams=" + valuesForParams +
               '}';
    }
}
