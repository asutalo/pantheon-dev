package com.eu.atit.mysql.query;

import static com.eu.atit.mysql.query.From.SPACE;

public class Join extends KeyWord implements QueryPart {
    static final String JOIN = " JOIN ";
    static final String ON = " ON ";
    static final String EQUALS = " = ";
    static final String DOT = ".";
    private final String targetTableName;
    private final String targetId;
    private final String sourceTableName;
    private final String sourceId;

    public Join(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        this.targetTableName = targetTableName;
        this.targetId = targetId;
        this.sourceTableName = sourceTableName;
        this.sourceId = sourceId;
    }

    @Override
    public String apply(String query) {
        return query.concat(JOIN).concat(targetTableName).concat(SPACE).concat(targetTableName).concat(ON).concat(targetTableName).concat(DOT).concat(targetId).concat(EQUALS).concat(sourceTableName).concat(DOT).concat(sourceId);
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
