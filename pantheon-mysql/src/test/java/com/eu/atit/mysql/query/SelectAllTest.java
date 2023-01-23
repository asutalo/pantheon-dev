//package com.eu.atit.mysql.query;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verifyNoInteractions;
//
//class SelectAllTest {
//    private static final String SOME_QUERY = "SOME_QUERY";
//
//    @Test
//    void apply() {
//        String expectedQuery = SOME_QUERY + SelectAll.SELECT;
//
//        assertEquals(expectedQuery, new SelectAll().apply(SOME_QUERY));
//    }
//
//    @Test
//    void applyOnPreparedStatement() throws SQLException {
//        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
//        new SelectAll().apply(mockPreparedStatement);
//
//        verifyNoInteractions(mockPreparedStatement);
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @Test
//    void isKeyWord() {
//        SelectAll selectAll = new SelectAll();
//        assertTrue(selectAll instanceof KeyWord);
//    }
//
//    @Test
//    void equals() {
//        SelectAll selectAll1 = new SelectAll();
//        SelectAll selectAll2 = new SelectAll();
//
//        Assertions.assertEquals(selectAll1, selectAll2);
//    }
//
//    @Test
//    void hashcode() {
//        SelectAll selectAll = new SelectAll();
//
//        Assertions.assertEquals(selectAll.hashCode(), selectAll.hashCode());
//    }
//}