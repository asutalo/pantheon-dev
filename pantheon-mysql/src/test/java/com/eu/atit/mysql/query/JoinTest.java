package com.eu.atit.mysql.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class JoinTest {
    private final Join join = getJoin();

    private static Join getJoin() {
        return new Join("someTable", "someKey", "someOtherTable", "someOtherKey");
    }

    @Test
    void apply() {
        String query = "query";
        String expectedQuery = query + System.lineSeparator() +
                "JOIN someTable AS sometable" + System.lineSeparator() +
                "\t\t\tON someothertable.someOtherKey = sometable.someKey";

        assertEquals(expectedQuery, join.apply(query));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        join.apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        assertTrue(join instanceof KeyWord);
    }

    @Test
    void equals() {
        Join join1 = getJoin();
        Join join2 = getJoin();

        assertEquals(join1, join2);
    }

    @Test
    void hashcode() {
        assertEquals(join.hashCode(), join.hashCode());
    }

}