package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;

import java.util.*;

public class SelectWithAliases extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT" + System.lineSeparator();
    static final String AS = " AS ";
    static final String SEPARATOR = ", ";
    private final Set<ColumnNameAndAlias> columnsAndAliases;

    public SelectWithAliases(Set<ColumnNameAndAlias> columnsAndAliases) {
        this.columnsAndAliases = new HashSet<>(columnsAndAliases);
    }

    @Override
    public String apply(String query) {
        StringBuilder selectionStringBuilder = new StringBuilder(query.concat(SELECT));

        ColumnNameAndAlias columnAndAlias = columnsAndAliases.iterator().next();

        selectionStringBuilder.append(columnAndAlias.fieldName());
        selectionStringBuilder.append(AS);
        selectionStringBuilder.append(columnAndAlias.alias());

        columnsAndAliases.remove(columnAndAlias);

        for (ColumnNameAndAlias cAndA : columnsAndAliases) {
            selectionStringBuilder.append(SEPARATOR);
            selectionStringBuilder.append(cAndA.fieldName());
            selectionStringBuilder.append(AS);
            selectionStringBuilder.append(cAndA.alias());
        }
        return selectionStringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectWithAliases that = (SelectWithAliases) o;
        return Objects.equals(columnsAndAliases, that.columnsAndAliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnsAndAliases);
    }
}
