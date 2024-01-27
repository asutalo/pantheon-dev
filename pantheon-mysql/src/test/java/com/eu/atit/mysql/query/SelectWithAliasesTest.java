package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

import static com.eu.atit.mysql.query.SelectWithAliases.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SelectWithAliasesTest {
    private static final String SOME_QUERY = "SOME_QUERY-";
    private static final String SOME_COL = "someCol";
    private static final String SOME_ALIAS = "someAlias";
    private static final String SOME_OTHER_COL = "someOtherCol";
    private static final String SOME_OTHER_ALIAS = "someOtherAlias";

    private static final LinkedHashSet<ColumnNameAndAlias> SOME_COLUMNS_AND_ALIASES = new LinkedHashSet<>(List.of(new ColumnNameAndAlias(SOME_COL, SOME_ALIAS), new ColumnNameAndAlias(SOME_OTHER_COL, SOME_OTHER_ALIAS)));

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + SELECT + SOME_COL + AS + SOME_ALIAS + SEPARATOR + SOME_OTHER_COL + AS + SOME_OTHER_ALIAS;

        assertEquals(expectedQuery, new SelectWithAliases(SOME_COLUMNS_AND_ALIASES).apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new SelectWithAliases(SOME_COLUMNS_AND_ALIASES).apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        SelectWithAliases select = new SelectWithAliases(SOME_COLUMNS_AND_ALIASES);
        assertTrue(select instanceof KeyWord);
    }

    @Test
    void equals() {
        SelectWithAliases select1 = new SelectWithAliases(SOME_COLUMNS_AND_ALIASES);
        SelectWithAliases select2 = new SelectWithAliases(SOME_COLUMNS_AND_ALIASES);

        Assertions.assertEquals(select1, select2);
    }

    @Test
    void hashcode() {
        SelectWithAliases select = new SelectWithAliases(SOME_COLUMNS_AND_ALIASES);

        Assertions.assertEquals(select.hashCode(), select.hashCode());
    }
}