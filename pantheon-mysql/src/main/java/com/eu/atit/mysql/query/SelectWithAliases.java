package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectWithAliases extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT" + System.lineSeparator();
    static final String AS = " AS ";
    static final String SEPARATOR = ", ";
    private final List<ColumnNameAndAlias> columnsAndAliases;

    public SelectWithAliases(List<ColumnNameAndAlias> columnsAndAliases) {
        this.columnsAndAliases = new ArrayList<>(columnsAndAliases);
    }

    @Override
    public String apply(String query) {
        StringBuilder selectionStringBuilder = new StringBuilder(query.concat(SELECT));

        ColumnNameAndAlias columnAndAlias = columnsAndAliases.get(0);

        selectionStringBuilder.append(columnAndAlias.fieldName());
        selectionStringBuilder.append(AS);
        selectionStringBuilder.append(columnAndAlias.alias());

        columnsAndAliases.remove(0);

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
