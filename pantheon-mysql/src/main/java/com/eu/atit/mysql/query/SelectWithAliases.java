package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class SelectWithAliases extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT\t";
    static final String AS = " AS ";
    static final String SEPARATOR = ",".concat(System.lineSeparator()).concat("\t\t");
    private final LinkedHashSet<ColumnNameAndAlias> columnsAndAliases;

    public SelectWithAliases(LinkedHashSet <ColumnNameAndAlias> columnsAndAliases) {
        this.columnsAndAliases = columnsAndAliases;
    }

    @Override
    public String apply(String query) {
        StringBuilder selectionStringBuilder = new StringBuilder(query.concat(SELECT));
        ArrayList<ColumnNameAndAlias> columnNameAndAliasSet = new ArrayList<>(columnsAndAliases); // todo LOL fix this

        ColumnNameAndAlias columnAndAlias = columnNameAndAliasSet.iterator().next();

        selectionStringBuilder.append(columnAndAlias.fieldName());
        selectionStringBuilder.append(AS);
        selectionStringBuilder.append(columnAndAlias.alias());

        columnNameAndAliasSet.remove(columnAndAlias);

        for (ColumnNameAndAlias cAndA : columnNameAndAliasSet) {

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

    @Override
    public String toString() {
        return "SelectWithAliases{" +
               "columnsAndAliases=" + columnsAndAliases +
               '}';
    }
}
