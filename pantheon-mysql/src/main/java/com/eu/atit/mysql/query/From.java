package com.eu.atit.mysql.query;

import java.util.Objects;

import static com.eu.atit.mysql.query.SelectWithAliases.AS;

public class From extends KeyWord implements QueryPart {
    private final String decorator;
    static final String FROM = System.lineSeparator() + "FROM\t";

    public From(String tableName) {
        this.decorator = FROM.concat(tableName).concat(AS).concat(tableName.toLowerCase());
    }

    @Override
    public String apply(String query) {
        return query.concat(decorator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        From from = (From) o;
        return Objects.equals(decorator, from.decorator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(decorator);
    }

    @Override
    public String toString() {
        return "From{" +
               "decorator='" + decorator + '\'' +
               '}';
    }
}
