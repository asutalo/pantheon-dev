package com.eu.atit.mysql.query;

import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

import static com.eu.atit.mysql.query.SelectWithAliases.AS;

public class Join extends KeyWord implements QueryPart {
    static final String JOIN = System.lineSeparator().concat("JOIN ");
    static final String ON = System.lineSeparator().concat("\t\t\t") + "ON ";
    static final String EQUALS = " = ";
    static final String DOT = ".";
    final String joinDecorator;

    public Join(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        String targetTableNameLowercase = targetTableName.toLowerCase();
        this.joinDecorator = targetTableName.concat(AS).concat(targetTableNameLowercase).concat(ON).concat(sourceTableName.toLowerCase()).concat(DOT).concat(sourceId).concat(EQUALS).concat(targetTableNameLowercase).concat(DOT).concat(targetId);
    }

    @Override
    public String apply(String query) {
        return joinQuery(query.concat(JOIN));
    }

    String joinQuery(String query) {
        return query.concat(joinDecorator);
    }

    @IgnoreCoverage
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @IgnoreCoverage
    @Override
    public int hashCode() {
        return 0;
    }

    @IgnoreCoverage
    @Override
    public String toString() {
        return "Join{" +
                "joinDecorator='" + joinDecorator +
                '}';
    }
}
