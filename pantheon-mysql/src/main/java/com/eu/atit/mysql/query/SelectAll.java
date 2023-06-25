package com.eu.atit.mysql.query;

import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

public class SelectAll extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT *";

    @Override
    public String apply(String query) {
        return query.concat(SELECT);
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
        return "SelectAll{}";
    }
}
