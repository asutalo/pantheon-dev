package com.eu.atit.mysql.query;

import static com.eu.atit.mysql.query.From.SPACE;

public class LeftJoin extends KeyWord implements QueryPart {
    static final String LEFT_JOIN =  System.lineSeparator() + "LEFT JOIN ";
    static final String ON = " ON ";
    static final String EQUALS = " = ";
    static final String DOT = ".";
    private final String targetTableName;
    private final String targetId;
    private final String sourceTableName;
    private final String sourceId;
    private final String targetTableNameLowercase;

    public LeftJoin(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        this.targetTableName = targetTableName;
        this.targetTableNameLowercase = targetTableName.toLowerCase();
        this.targetId = targetId;
        this.sourceTableName = sourceTableName;
        this.sourceId = sourceId;
    }

    @Override
    public String apply(String query) {
        return query.concat(LEFT_JOIN).concat(targetTableName).concat(SPACE).concat(targetTableNameLowercase).concat(ON).concat(sourceTableName).concat(DOT).concat(sourceId).concat(EQUALS).concat(targetTableNameLowercase).concat(DOT).concat(targetId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
