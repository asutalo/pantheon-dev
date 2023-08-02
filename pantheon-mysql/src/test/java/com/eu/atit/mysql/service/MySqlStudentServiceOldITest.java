package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.Insert;
import com.eu.atit.mysql.query.KeyVal;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.mysql.query.QueryPart;
import com.eu.atit.mysql.test.model.Type;
import com.google.inject.TypeLiteral;
import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MySqlStudentServiceOldITest {
    @Mock
    private MySqlClient mockMySqlClient;

    @InjectMocks
    private MySQLServiceProvider mySQLServiceProvider;

    @Captor
    ArgumentCaptor<QueryBuilder> queryBuilderCaptor;

    @DisplayName("Type")
    @Nested
    class TypeTest {
        private MySQLService<Type> mySqlService;
        private static final Class<Type> TEST_CLASS = Type.class;
        private static final String TEST_CLASS_NAME = TEST_CLASS.getSimpleName();
        private static final String TEST_CLASS_NAME_LOWERCASE = TEST_CLASS_NAME.toLowerCase();

        private static final String ID_FIELD = "id";
        private static final String NAME_FIELD = "name";

        private static final String SOME_NAME = "someName";

        private static final int SOME_ID_FIELD_VALUE = 1;
        private static final String SELECT_ID_FIELD = getSelector(TEST_CLASS, ID_FIELD);
        private static final String ID_FIELD_ALIAS = getAlias(TEST_CLASS, ID_FIELD);
        private static final String SELECT_NAME_FIELD = getSelector(TEST_CLASS, NAME_FIELD);
        private static final String NAME_FIELD_ALIAS = getAlias(TEST_CLASS, NAME_FIELD);


        @BeforeEach
        void setUp() {
            mySqlService = (MySQLService<Type>) mySQLServiceProvider.provide(TypeLiteral.get(TEST_CLASS));
        }

        @DisplayName("Insert")
        @Nested
        class InsertTest {
            @Test
            void shouldInsert_andSetGeneratedId() throws SQLException {
                String expectedQuery = String.format("INSERT INTO %s (%s) VALUES (?);", TEST_CLASS_NAME, NAME_FIELD);
                Type type = new Type(SOME_NAME);
                when(mockMySqlClient.executeInsertQuery(any())).thenReturn(SOME_ID_FIELD_VALUE);

                mySqlService.save(type);

                verify(mockMySqlClient).executeInsertQuery(queryBuilderCaptor.capture());
                QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
                assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
                List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
                assertEquals(1, queryParts.size());
                Insert insertQueryPart = (Insert) queryParts.get(0);
                assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, NAME_FIELD, SOME_NAME, 1)), insertQueryPart.getValuesForParams());
                assertEquals(SOME_ID_FIELD_VALUE, type.getId());
            }
        }

        @DisplayName("Select")
        @Nested
        class SelectTest {
            @Test
            void shouldSelectAll() throws SQLException {
                String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s;", SELECT_ID_FIELD, ID_FIELD_ALIAS, SELECT_NAME_FIELD, NAME_FIELD_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE);

                String otherName = "other name";
                int otherId = 2;
                Type expectedType = new Type(SOME_ID_FIELD_VALUE, SOME_NAME);
                Type expectedOtherType = new Type(otherId, otherName);
                when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, NAME_FIELD_ALIAS, SOME_NAME), Map.of(ID_FIELD_ALIAS, otherId, NAME_FIELD_ALIAS, otherName)));

                List<Type> actualTypes = mySqlService.getAll();

                assertQuery(expectedQuery);
                assertEquals(List.of(expectedType, expectedOtherType), actualTypes);
            }
            @Test
            void shouldSelectAll_withMapFilter() throws SQLException {
                String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s WHERE %s = ? AND %s = ?;", SELECT_ID_FIELD, ID_FIELD_ALIAS, SELECT_NAME_FIELD, NAME_FIELD_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, SELECT_ID_FIELD, SELECT_NAME_FIELD);
                Type expectedType = new Type(SOME_ID_FIELD_VALUE, SOME_NAME);
                when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, NAME_FIELD_ALIAS, SOME_NAME)));

                LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
                filter.put(SELECT_ID_FIELD, SOME_ID_FIELD_VALUE);
                filter.put(SELECT_NAME_FIELD, SOME_NAME);

                List<Type> actualTypes = mySqlService.getAll(filter);


                List<KeyVal> keyValues = assertQuery(expectedQuery).getKeyValues();
                assertEquals(List.of(expectedType), actualTypes);
                assertEquals(2, keyValues.size());
                assertEquals(SOME_ID_FIELD_VALUE, keyValues.get(0).getValue());
                assertEquals(SOME_NAME, keyValues.get(1).getValue());
            }
            @Test
            void shouldSelect_withMapFilter() throws SQLException {
                String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s WHERE %s = ? AND %s = ?;", SELECT_ID_FIELD, ID_FIELD_ALIAS, SELECT_NAME_FIELD, NAME_FIELD_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, SELECT_ID_FIELD, SELECT_NAME_FIELD);

                Type expectedType = new Type(SOME_ID_FIELD_VALUE, SOME_NAME);
                when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, NAME_FIELD_ALIAS, SOME_NAME)));

                LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
                filter.put(SELECT_ID_FIELD, SOME_ID_FIELD_VALUE);
                filter.put(SELECT_NAME_FIELD, SOME_NAME);

                Type actualType = mySqlService.get(filter);


                List<KeyVal> keyValues = assertQuery(expectedQuery).getKeyValues();
                assertEquals(expectedType, actualType);
                assertEquals(2, keyValues.size());
                assertEquals(SOME_ID_FIELD_VALUE, keyValues.get(0).getValue());
                assertEquals(SOME_NAME, keyValues.get(1).getValue());
            }
        }
    }

    private QueryBuilder assertQuery(String expectedQuery) throws SQLException {
        verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
        QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
        assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
        return actualQueryBuilder;
    }

    private String getActualQuery(QueryBuilder actualQueryBuilder) {
        return actualQueryBuilder.buildQueryString().replaceAll(System.lineSeparator(), " ").replaceAll("\\t", " ").replaceAll(" +", " ");
    }

    private static String getAlias(Class<?> testClass, String fieldName) {
        return testClass.getSimpleName().toLowerCase().concat("_").concat(fieldName);
    }

    private static String getSelector(Class<?> testClass, String fieldName) {
        return testClass.getSimpleName().toLowerCase().concat(".").concat(fieldName);
    }
}
