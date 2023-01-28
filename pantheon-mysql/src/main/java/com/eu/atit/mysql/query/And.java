package com.eu.atit.mysql.query;


public class And extends KeyWord implements QueryPart {
    static final String AND = System.lineSeparator().concat("\t\tAND ");

    @Override
    public String apply(String query) {
        return query.concat(AND);
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
        return "And{}";
    }
}
