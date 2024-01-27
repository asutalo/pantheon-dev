package com.eu.atit.mysql.query;

import com.eu.atit.mysql.service.ColumnNameAndAlias;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import static com.eu.atit.mysql.query.And.AND;
import static com.eu.atit.mysql.query.Delete.DELETE;
import static com.eu.atit.mysql.query.From.FROM;
import static com.eu.atit.mysql.query.Insert.*;
import static com.eu.atit.mysql.query.Join.*;
import static com.eu.atit.mysql.query.KeyVal.IS_VAL;
import static com.eu.atit.mysql.query.QueryBuilder.QUERY_END;
import static com.eu.atit.mysql.query.SelectWithAliases.*;
import static com.eu.atit.mysql.query.Update.*;
import static com.eu.atit.mysql.query.Where.WHERE;
import static com.mysql.cj.MysqlType.INT;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {
    public static final String SOME_KEY = "SOME_KEY";
    public static final String SOME_OTHER_KEY = "SOME_OTHER_KEY";
    public static final String ADDITIONAL_KEY = "KEY";
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_WHERE_KEY = "SOME_WHERE_KEY";

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private MySqlValue mockMySqlValue;

    @Test
    void buildSelectAllQuery() {
        String someOtherTable = "someOtherTable";

        String expectedQuery = SelectAll.SELECT + FROM + SOME_TABLE + AS + SOME_TABLE.toLowerCase() + JOIN + someOtherTable + AS + someOtherTable.toLowerCase() + ON + SOME_TABLE.toLowerCase() + DOT + SOME_OTHER_KEY + EQUALS + someOtherTable.toLowerCase() + DOT + SOME_KEY + WHERE + SOME_WHERE_KEY + IS_VAL + AND + SOME_OTHER_KEY + IS_VAL + QUERY_END;
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from(SOME_TABLE);
        queryBuilder.join(someOtherTable, SOME_KEY, SOME_TABLE, SOME_OTHER_KEY);
        queryBuilder.where();
        queryBuilder.keyIsVal(mockMySqlValue);
        queryBuilder.and();
        queryBuilder.keyIsVal(mockMySqlValue);

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue, never()).setParamIndex(anyInt());
    }

    @Test
    void buildSelectQueryWithColumnsAndAliases() {
        String SOME_ALIAS = "someAlias";
        String SOME_OTHER_ALIAS = "someOtherAlias";

        LinkedHashSet<ColumnNameAndAlias> someColumnsAndAliases = new LinkedHashSet<>() {{
            add(new ColumnNameAndAlias(SOME_KEY, SOME_ALIAS));
            add(new ColumnNameAndAlias(SOME_OTHER_KEY, SOME_OTHER_ALIAS));
        }};

        String expectedQuery = SELECT + SOME_KEY + AS + SOME_ALIAS + SEPARATOR + SOME_OTHER_KEY + AS + SOME_OTHER_ALIAS + FROM + SOME_TABLE + AS + SOME_TABLE.toLowerCase() + WHERE + SOME_WHERE_KEY + IS_VAL + AND + SOME_OTHER_KEY + IS_VAL + QUERY_END;
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(someColumnsAndAliases);
        queryBuilder.from(SOME_TABLE);
        queryBuilder.where();
        queryBuilder.keyIsVal(mockMySqlValue);
        queryBuilder.and();
        queryBuilder.keyIsVal(mockMySqlValue);

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue, never()).setParamIndex(anyInt());
    }

    @Test
    void buildInsertQuery() {
        String expectedQuery = INSERT + SOME_TABLE + COLUMNS_START + SOME_WHERE_KEY + Insert.DELIMITER + SOME_OTHER_KEY + COLUMNS_END + VALUES_START + PLACEHOLDER + Insert.DELIMITER + PLACEHOLDER + VALUES_END + QUERY_END;
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert(SOME_TABLE, new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue)));

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue).setParamIndex(1);
        verify(mockMySqlValue).setParamIndex(2);

    }

    @Test
    void buildUpdateQuery() {
        String expectedQuery = UPDATE + SOME_TABLE + SET + SOME_KEY + IS_VAL + Update.DELIMITER + SOME_OTHER_KEY + IS_VAL + WHERE + SOME_WHERE_KEY + IS_VAL + AND + ADDITIONAL_KEY + IS_VAL + QUERY_END;

        MySqlValue mySqlValue = new MySqlValue(INT, SOME_KEY, 2);
        MySqlValue mySqlValue1 = new MySqlValue(INT, SOME_OTHER_KEY, 2);
        MySqlValue mySqlValue2 = new MySqlValue(INT, SOME_WHERE_KEY, 3);
        MySqlValue mySqlValue3 = new MySqlValue(INT, ADDITIONAL_KEY, 4);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.update(SOME_TABLE, new LinkedList<>(List.of(mySqlValue, mySqlValue1)));
        queryBuilder.where();
        queryBuilder.keyIsVal(mySqlValue2);
        queryBuilder.and();
        queryBuilder.keyIsVal(mySqlValue3);


        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        Assertions.assertEquals(1, mySqlValue.getParamIndex());
        Assertions.assertEquals(2, mySqlValue1.getParamIndex());
        Assertions.assertEquals(0, mySqlValue2.getParamIndex());
        Assertions.assertEquals(0, mySqlValue3.getParamIndex());
    }

    @Test
    void buildDeleteQuery() {
        String expectedQuery = DELETE + FROM + SOME_TABLE + AS + SOME_TABLE.toLowerCase() + WHERE + SOME_WHERE_KEY + IS_VAL + AND + SOME_OTHER_KEY + IS_VAL + QUERY_END;

        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from(SOME_TABLE);
        queryBuilder.where();
        queryBuilder.keyIsVal(mockMySqlValue);
        queryBuilder.and();
        queryBuilder.keyIsVal(mockMySqlValue);

        verify(mockMySqlValue, never()).setParamIndex(anyInt());
        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @SuppressWarnings("MagicConstant")
    @Test
    void buildStatement() throws SQLException {
        QueryPart mockQueryPart = mock(QueryPart.class);
        QueryBuilder queryBuilder = new QueryBuilder();

        queryBuilder.addQueryParts(List.of(mockQueryPart, mockQueryPart));

        when(mockQueryPart.apply(anyString())).thenReturn("query");
        when(mockConnection.prepareStatement(anyString(), eq(RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);

        queryBuilder.prepareStatement(mockConnection);

        verify(mockQueryPart, times(2)).apply(mockPreparedStatement);
    }

    @Test
    void equals() {
        QueryBuilder queryBuilder1 = new QueryBuilder();
        queryBuilder1.selectAll();
        queryBuilder1.from(SOME_TABLE);

        QueryBuilder queryBuilder2 = new QueryBuilder();
        queryBuilder2.selectAll();
        queryBuilder2.from(SOME_TABLE);

        Assertions.assertEquals(queryBuilder1, queryBuilder2);
    }

    @Test
    void equalsReturnsFalseWhenOrderOfApplicationDiffers() {
        QueryBuilder correct = new QueryBuilder();
        correct.selectAll();
        correct.from(SOME_TABLE);

        QueryBuilder incorrect = new QueryBuilder();
        incorrect.from(SOME_TABLE);
        incorrect.selectAll();

        Assertions.assertNotEquals(correct, incorrect);
    }

    @Test
    void hashcode() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from(SOME_TABLE);

        Assertions.assertEquals(queryBuilder.hashCode(), queryBuilder.hashCode());
    }
}