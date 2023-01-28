package com.eu.atit.mysql.query;

import static com.eu.atit.mysql.query.SelectWithAliases.AS;

public class Join extends KeyWord implements QueryPart {
    static final String JOIN = System.lineSeparator().concat("JOIN ");
    static final String ON = "ON ";
    static final String EQUALS = " = ";
    static final String DOT = ".";
    private final String targetTableName;
    private final String targetId;
    private final String sourceTableName;
    private final String sourceId;
    private final String targetTableNameLowercase;

    public Join(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        this.targetTableName = targetTableName;
        this.targetTableNameLowercase = targetTableName.toLowerCase();
        this.targetId = targetId;
        this.sourceTableName = sourceTableName;
        this.sourceId = sourceId;
    }

    @Override
    public String apply(String query) {
        return joinQuery(query.concat(JOIN));
    }

    String joinQuery(String query) {
        return query.concat(targetTableName).concat(AS).concat(targetTableNameLowercase).concat(System.lineSeparator()).concat("\t\t\t").concat(ON).concat(sourceTableName).concat(DOT).concat(sourceId).concat(EQUALS).concat(targetTableNameLowercase).concat(DOT).concat(targetId);
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

    @Override
    public String toString() {
        return "Join{" +
                "targetTableName='" + targetTableName + '\'' +
                ", targetId='" + targetId + '\'' +
                ", sourceTableName='" + sourceTableName + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", targetTableNameLowercase='" + targetTableNameLowercase + '\'' +
                '}';
    }
}
