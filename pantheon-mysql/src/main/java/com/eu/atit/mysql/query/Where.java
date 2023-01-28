package com.eu.atit.mysql.query;

public class Where extends KeyWord implements QueryPart {
    static final String WHERE = System.lineSeparator().concat("WHERE\t");

    public Where() {
    }

    @Override
    public String apply(String query) {
        return query.concat(WHERE);
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
        return "Where{}";
    }
}
