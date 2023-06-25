package com.eu.atit.mysql.query;

import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.eu.atit.mysql.query.KeyVal.IS_VAL;

public class Update implements QueryPart {
    static final String UPDATE = "UPDATE ".concat("\t");
    static final String DELIMITER = "," + System.lineSeparator() + "\t\t";
    private final String tableName;
    private final List<MySqlValue> valuesForQuery;
    private final List<MySqlValue> valuesForParams;

    static final String SET = System.lineSeparator().concat("SET\t\t");

    private final String updateDecorator;


    public Update(String tableName, LinkedList<MySqlValue> values) {
        this.tableName = tableName;
        this.valuesForQuery = new LinkedList<>(values);
        this.valuesForParams = new LinkedList<>(values);
        this.updateDecorator = UPDATE.concat(tableName).concat(SET);
    }

    @Override
    public String apply(String query) {
        List<MySqlValue> values = new ArrayList<>(valuesForQuery); // todo LOL fix this
        MySqlValue mySqlValue = values.get(0);

        StringBuilder keysPlaceholderBuilder = new StringBuilder(mySqlValue.getKey().concat(IS_VAL));

        values.remove(0);

        for (MySqlValue value : values) {
            keysPlaceholderBuilder.append(DELIMITER);
            keysPlaceholderBuilder.append(value.getKey().concat(IS_VAL));
        }

        return query.concat(updateDecorator).concat(keysPlaceholderBuilder.toString());
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

        final Update update = (Update) o;

        if (!Objects.equals(tableName, update.tableName)) return false;
        if (!Objects.equals(valuesForQuery, update.valuesForQuery))
            return false;
        if (!Objects.equals(valuesForParams, update.valuesForParams))
            return false;
        return Objects.equals(updateDecorator, update.updateDecorator);
    }

    @IgnoreCoverage
    @Override
    public int hashCode() {
        int result = tableName != null ? tableName.hashCode() : 0;
        result = 31 * result + (valuesForQuery != null ? valuesForQuery.hashCode() : 0);
        result = 31 * result + (valuesForParams != null ? valuesForParams.hashCode() : 0);
        result = 31 * result + (updateDecorator != null ? updateDecorator.hashCode() : 0);
        return result;
    }

    @IgnoreCoverage
    @Override
    public String toString() {
        return "Update{" +
               "tableName='" + tableName + '\'' +
               ", valuesForQuery=" + valuesForQuery +
               ", valuesForParams=" + valuesForParams +
               ", updateDecorator='" + updateDecorator + '\'' +
               '}';
    }
}
