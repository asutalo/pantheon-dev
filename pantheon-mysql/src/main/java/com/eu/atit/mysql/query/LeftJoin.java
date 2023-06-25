package com.eu.atit.mysql.query;


import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

public class LeftJoin extends Join implements QueryPart {
    static final String LEFT = System.lineSeparator().concat("LEFT JOIN ");

    public LeftJoin(String targetTableName, String targetId, String sourceTableName, String sourceId) {
        super(targetTableName, targetId, sourceTableName, sourceId);
    }

    @Override
    public String apply(String query) {
        return joinQuery(query.concat(LEFT));
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
}
