package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.*;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.google.inject.TypeLiteral;
import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MySQLServiceITest {
    @Mock
    private MySqlClient mockMySqlClient;
    @InjectMocks
    private MySQLServiceProvider mySQLServiceProvider;

    @Captor
    ArgumentCaptor<QueryBuilder> queryBuilderCaptor;

    @DisplayName("Non class specific methods")
    @Nested
    class NonSpecificMethodsTest {
        private static final String ID = "id";
        private static final Class<SomeTestTarget> TEST_CLASS = SomeTestTarget.class;
        private static final String TEST_CLASS_NAME = TEST_CLASS.getSimpleName();
        private MySQLService<SomeTestTarget> mySqlService;

        @BeforeEach
        void setUp() {
            mySqlService = (MySQLService<SomeTestTarget>) mySQLServiceProvider.provide(TypeLiteral.get(TEST_CLASS));
        }

        @Test
        void shouldSelect_withFilteredSelect() throws SQLException {
            String someTableName = "SomeTableName";
            String expectedQuery = String.format("SELECT * FROM %s AS %s;", someTableName, someTableName.toLowerCase());
            Class<SomeTestTarget> testClass = SomeTestTarget.class;
            MySQLService<SomeTestTarget> mySqlService = (MySQLService<SomeTestTarget>) mySQLServiceProvider.provide(TypeLiteral.get(testClass));
            QueryBuilder filteredSelect = new QueryBuilder();
            filteredSelect.selectAll();
            filteredSelect.from(someTableName);
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(getAlias(testClass, ID), 1)));

            mySqlService.get(filteredSelect);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
        }

        @Test
        void select_withFilteredSelect_shouldThrowExceptionWhenNoElementsFound() throws SQLException {
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of());

            Assertions.assertThrows(IllegalStateException.class, () -> mySqlService.get(new QueryBuilder()));
        }

        @Test
        void select_withFilteredSelect_shouldThrowExceptionWhenMoreThanOneElementsFound() throws SQLException {
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(), Map.of()));

            Assertions.assertThrows(IllegalStateException.class, () -> mySqlService.get(new QueryBuilder()));
        }


        @Test
        void select_withMapFilter_shouldThrowExceptionWhenNoElementsFound() throws SQLException {
            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(getSelector(TEST_CLASS, "id"), 1);
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of());

            IllegalStateException actualException = assertThrows(IllegalStateException.class, () -> mySqlService.get(filter));
            assertEquals("No elements found", actualException.getMessage());
        }

        @Test
        void select_withMapFilter_shouldThrowExceptionWhenMoreThanOneElementsFound() throws SQLException {
            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(getSelector(TEST_CLASS, "id"), 1);
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(), Map.of()));

            IllegalStateException actualException = assertThrows(IllegalStateException.class, () -> mySqlService.get(filter));
            assertEquals("No elements found", actualException.getMessage());
        }

        @Test
        void select_withMapFilter_shouldThrowExceptionWhenNoFiltersMatch() {
            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();

            IllegalStateException actualException = assertThrows(IllegalStateException.class, () -> mySqlService.get(filter));
            assertEquals("Provided filters do not match any attribute", actualException.getMessage());
        }

        @Test
        void update_shouldThrowException_whenNoRowsAffected() throws SQLException {
            SomeTestTarget noNesting = new SomeTestTarget(1);
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(0);

            Exception exception = assertThrows(RuntimeException.class, () -> mySqlService.update(noNesting));
            assertEquals("Update failed", exception.getMessage());
        }

        @Test
        void delete_shouldThrowException_whenNoRowsAffected() throws SQLException {
            SomeTestTarget noNesting = new SomeTestTarget(1);
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(0);

            Exception exception = assertThrows(RuntimeException.class, () -> mySqlService.delete(noNesting));
            assertEquals("Deletion failed", exception.getMessage());
        }

        private static class SomeTestTarget {
            @MySqlField(type = MysqlType.INT, primary = true)
            private int id;

            public SomeTestTarget(int id) {
                this.id = id;
            }

            public SomeTestTarget() {
            }
        }
    }

    @DisplayName("Working with flat objects - no column names")
    @Nested
    class NoNestedObjectsNoColumnNamesTest {
        private static final Class<NoNesting> TEST_CLASS = NoNesting.class;
        private static final String TEST_CLASS_NAME = TEST_CLASS.getSimpleName();
        private static final String TEST_CLASS_NAME_LOWERCASE = TEST_CLASS_NAME.toLowerCase();

        private static final String ID_FIELD = "idField";
        private static final String SOME_STRING = "someString";
        private static final String SOME_STRING_VALUE = "someStringValue";
        private static final int SOME_ID_FIELD_VALUE = 1;
        private static final String SELECT_ID_FIELD = getSelector(TEST_CLASS, ID_FIELD);
        private static final String ID_FIELD_ALIAS = getAlias(TEST_CLASS, ID_FIELD);
        private static final String SELECT_SOME_STRING = getSelector(TEST_CLASS, SOME_STRING);
        private static final String SOME_STRING_ALIAS = getAlias(TEST_CLASS, SOME_STRING);
        private MySQLService<NoNesting> mySqlService;

        @BeforeEach
        void setUp() {
            mySqlService = (MySQLService<NoNesting>) mySQLServiceProvider.provide(TypeLiteral.get(TEST_CLASS));
        }

        @Test
        void shouldSelect_withMapFilter() throws SQLException {
            String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s WHERE %s = ? AND %s = ?;", SELECT_ID_FIELD, ID_FIELD_ALIAS, SELECT_SOME_STRING, SOME_STRING_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, SELECT_ID_FIELD, SELECT_SOME_STRING);

            NoNesting expectedNoNesting = new NoNesting(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, SOME_STRING_ALIAS, SOME_STRING_VALUE)));

            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(SELECT_ID_FIELD, SOME_ID_FIELD_VALUE);
            filter.put(SELECT_SOME_STRING, SOME_STRING_VALUE);

            NoNesting actualNoNesting = mySqlService.get(filter);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));

            assertEquals(expectedNoNesting, actualNoNesting);
            List<KeyVal> keyValues = actualQueryBuilder.getKeyValues();
            assertEquals(2, keyValues.size());
            assertEquals(SOME_ID_FIELD_VALUE, keyValues.get(0).getValue());
            assertEquals(SOME_STRING_VALUE, keyValues.get(SOME_ID_FIELD_VALUE).getValue());
        }

        @Test
        void shouldSelectAll() throws SQLException {
            String someOtherStringValue = SOME_STRING_VALUE + 2;
            int someOtherIdFieldValue = 2;
            String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s;", SELECT_ID_FIELD, ID_FIELD_ALIAS, SELECT_SOME_STRING, SOME_STRING_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE);
            NoNesting expectedNoNesting = new NoNesting(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            NoNesting expectedNoNesting1 = new NoNesting(someOtherIdFieldValue, someOtherStringValue);
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, SOME_STRING_ALIAS, SOME_STRING_VALUE), Map.of(ID_FIELD_ALIAS, someOtherIdFieldValue, SOME_STRING_ALIAS, someOtherStringValue)));

            List<NoNesting> actualNoNestings = mySqlService.getAll();

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
            assertEquals(List.of(expectedNoNesting, expectedNoNesting1), actualNoNestings);
        }

        @Test
        void shouldSelectAll_withMapFilter() throws SQLException {
            String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s WHERE %s = ? AND %s = ?;", SELECT_ID_FIELD, ID_FIELD_ALIAS, SELECT_SOME_STRING, SOME_STRING_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, SELECT_ID_FIELD, SELECT_SOME_STRING);

            NoNesting expectedNoNesting = new NoNesting(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, SOME_STRING_ALIAS, SOME_STRING_VALUE)));

            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(SELECT_ID_FIELD, SOME_ID_FIELD_VALUE);
            filter.put(SELECT_SOME_STRING, SOME_STRING_VALUE);

            List<NoNesting> actualNoNesting = mySqlService.getAll(filter);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));

            assertEquals(List.of(expectedNoNesting), actualNoNesting);
            List<KeyVal> keyValues = actualQueryBuilder.getKeyValues();
            assertEquals(2, keyValues.size());
            assertEquals(SOME_ID_FIELD_VALUE, keyValues.get(0).getValue());
            assertEquals(SOME_STRING_VALUE, keyValues.get(SOME_ID_FIELD_VALUE).getValue());
        }

        @Test
        void shouldSave_andSetGeneratedId() throws SQLException {
            String expectedQuery = String.format("INSERT INTO %s (%s) VALUES (?);", TEST_CLASS_NAME, SOME_STRING);
            NoNesting noNesting = new NoNesting(SOME_STRING_VALUE);
            when(mockMySqlClient.executeInsertQuery(any())).thenReturn(SOME_ID_FIELD_VALUE);

            mySqlService.save(noNesting);

            verify(mockMySqlClient).executeInsertQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(1, queryParts.size());
            Insert insertQueryPart = (Insert) queryParts.get(0);
            assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, SOME_STRING, SOME_STRING_VALUE, 1)), insertQueryPart.getValuesForParams());
            assertEquals(SOME_ID_FIELD_VALUE, noNesting.idField);
        }

        @Test
        void shouldUpdate() throws SQLException {
            String expectedQuery = String.format("UPDATE %s SET %s = ? WHERE idField = ?;", TEST_CLASS_NAME, SOME_STRING);
            NoNesting noNesting = new NoNesting(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);

            mySqlService.update(noNesting);

            verify(mockMySqlClient).executeOtherDmlQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(3, queryParts.size());
            Update updateQueryPart = (Update) queryParts.get(0);
            assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, SOME_STRING, SOME_STRING_VALUE, 1)), updateQueryPart.getValuesForParams());
            KeyVal keyVal = (KeyVal) queryParts.get(2);
            assertEquals(SOME_ID_FIELD_VALUE, keyVal.getValue());
        }

        @Test
        void shouldDelete() throws SQLException {
            String expectedQuery = String.format("DELETE FROM %s AS %s WHERE %s = ?;", TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, ID_FIELD);
            NoNesting noNesting = new NoNesting(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);

            mySqlService.delete(noNesting);

            verify(mockMySqlClient).executeOtherDmlQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(4, queryParts.size());
            KeyVal keyVal = (KeyVal) queryParts.get(3);
            assertEquals(SOME_ID_FIELD_VALUE, keyVal.getValue());
        }


        private static class NoNesting {
            @MySqlField(type = MysqlType.INT, primary = true)
            private int idField;

            @MySqlField(type = MysqlType.VARCHAR)
            private String someString;

            public NoNesting(int idField, String someString) {
                this.idField = idField;
                this.someString = someString;
            }

            public NoNesting() {
            }

            public NoNesting(String someString) {
                this.someString = someString;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                NoNesting noNesting = (NoNesting) o;

                if (idField != noNesting.idField) return false;
                return Objects.equals(someString, noNesting.someString);
            }

            @Override
            public int hashCode() {
                int result = idField;
                result = 31 * result + (someString != null ? someString.hashCode() : 0);
                return result;
            }
        }
    }

    @DisplayName("Working with flat objects - with column names")
    @Nested
    class NoNestedObjectsWithColumnNamesTest {
        private static final Class<NoNestingNamedColumns> TEST_CLASS = NoNestingNamedColumns.class;
        private static final String TEST_CLASS_NAME = TEST_CLASS.getSimpleName();
        private static final String TEST_CLASS_NAME_LOWERCASE = TEST_CLASS_NAME.toLowerCase();
        private static final String NAMED_ID_FIELD = "namedIdColumn";
        private static final String NAMED_SOME_STRING = "namedSomeStringColumn";
        private static final String SOME_STRING_VALUE = "someStringValue";
        private static final int SOME_ID_FIELD_VALUE = 1;
        private static final String SELECT_NAMED_ID_FIELD = getSelector(TEST_CLASS, NAMED_ID_FIELD);
        private static final String NAMED_ID_FIELD_ALIAS = getAlias(TEST_CLASS, NAMED_ID_FIELD);
        private static final String SELECT_NAMED_SOME_STRING = getSelector(TEST_CLASS, NAMED_SOME_STRING);
        private static final String NAMED_SOME_STRING_ALIAS = getAlias(TEST_CLASS, NAMED_SOME_STRING);
        private MySQLService<NoNestingNamedColumns> mySqlService;

        @BeforeEach
        void setUp() {
            mySqlService = (MySQLService<NoNestingNamedColumns>) mySQLServiceProvider.provide(TypeLiteral.get(TEST_CLASS));
        }

        @Test
        void shouldSelect_withMapFilter() throws SQLException {
            String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s WHERE %s = ? AND %s = ?;", SELECT_NAMED_ID_FIELD, NAMED_ID_FIELD_ALIAS, SELECT_NAMED_SOME_STRING, NAMED_SOME_STRING_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, SELECT_NAMED_ID_FIELD, SELECT_NAMED_SOME_STRING);

            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(NAMED_ID_FIELD_ALIAS, SOME_ID_FIELD_VALUE, NAMED_SOME_STRING_ALIAS, SOME_STRING_VALUE)));

            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(SELECT_NAMED_ID_FIELD, SOME_ID_FIELD_VALUE);
            filter.put(SELECT_NAMED_SOME_STRING, SOME_STRING_VALUE);

            mySqlService.get(filter);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));

            List<KeyVal> keyValues = actualQueryBuilder.getKeyValues();
            assertEquals(2, keyValues.size());
            assertEquals(SOME_ID_FIELD_VALUE, keyValues.get(0).getValue());
            assertEquals(SOME_STRING_VALUE, keyValues.get(SOME_ID_FIELD_VALUE).getValue());
        }

        @Test
        void shouldSelectAll() throws SQLException {
            String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s;", SELECT_NAMED_ID_FIELD, NAMED_ID_FIELD_ALIAS, SELECT_NAMED_SOME_STRING, NAMED_SOME_STRING_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE);

            mySqlService.getAll();

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
        }

        @Test
        void shouldSelectAll_withMapFilter() throws SQLException {
            String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s AS %s WHERE %s = ? AND %s = ?;", SELECT_NAMED_ID_FIELD, NAMED_ID_FIELD_ALIAS, SELECT_NAMED_SOME_STRING, NAMED_SOME_STRING_ALIAS, TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, SELECT_NAMED_ID_FIELD, SELECT_NAMED_SOME_STRING);

            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(SELECT_NAMED_ID_FIELD, SOME_ID_FIELD_VALUE);
            filter.put(SELECT_NAMED_SOME_STRING, SOME_STRING_VALUE);

            mySqlService.getAll(filter);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));

            List<KeyVal> keyValues = actualQueryBuilder.getKeyValues();
            assertEquals(2, keyValues.size());
            assertEquals(SOME_ID_FIELD_VALUE, keyValues.get(0).getValue());
            assertEquals(SOME_STRING_VALUE, keyValues.get(SOME_ID_FIELD_VALUE).getValue());
        }

        @Test
        void shouldSave_andSetGeneratedId() throws SQLException {
            String expectedQuery = String.format("INSERT INTO %s (%s) VALUES (?);", TEST_CLASS_NAME, NAMED_SOME_STRING);
            NoNestingNamedColumns noNestingNamedColumns = new NoNestingNamedColumns(SOME_STRING_VALUE);
            when(mockMySqlClient.executeInsertQuery(any())).thenReturn(SOME_ID_FIELD_VALUE);

            mySqlService.save(noNestingNamedColumns);

            verify(mockMySqlClient).executeInsertQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(1, queryParts.size());
            Insert insertQueryPart = (Insert) queryParts.get(0);
            assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, NAMED_SOME_STRING, SOME_STRING_VALUE, 1)), insertQueryPart.getValuesForParams());
            assertEquals(SOME_ID_FIELD_VALUE, noNestingNamedColumns.idField);
        }

        @Test
        void shouldUpdate() throws SQLException {
            String expectedQuery = String.format("UPDATE %s SET %s = ? WHERE %s = ?;", TEST_CLASS_NAME, NAMED_SOME_STRING, NAMED_ID_FIELD);
            NoNestingNamedColumns noNestingNamedColumns = new NoNestingNamedColumns(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);

            mySqlService.update(noNestingNamedColumns);

            verify(mockMySqlClient).executeOtherDmlQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(3, queryParts.size());
            Update updateQueryPart = (Update) queryParts.get(0);
            assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, NAMED_SOME_STRING, SOME_STRING_VALUE, 1)), updateQueryPart.getValuesForParams());
            KeyVal keyVal = (KeyVal) queryParts.get(2);
            assertEquals(SOME_ID_FIELD_VALUE, keyVal.getValue());
        }

        @Test
        void shouldDelete() throws SQLException {
            String expectedQuery = String.format("DELETE FROM %s AS %s WHERE %s = ?;", TEST_CLASS_NAME, TEST_CLASS_NAME_LOWERCASE, NAMED_ID_FIELD);
            NoNestingNamedColumns noNestingNamedColumns = new NoNestingNamedColumns(SOME_ID_FIELD_VALUE, SOME_STRING_VALUE);
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);

            mySqlService.delete(noNestingNamedColumns);

            verify(mockMySqlClient).executeOtherDmlQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(4, queryParts.size());
            KeyVal keyVal = (KeyVal) queryParts.get(3);
            assertEquals(SOME_ID_FIELD_VALUE, keyVal.getValue());
        }


        private static class NoNestingNamedColumns {
            @MySqlField(type = MysqlType.INT, primary = true, column = "namedIdColumn")
            private int idField;

            @MySqlField(type = MysqlType.VARCHAR, column = "namedSomeStringColumn")
            private String someString;

            public NoNestingNamedColumns(int idField, String someString) {
                this.idField = idField;
                this.someString = someString;
            }

            public NoNestingNamedColumns() {
            }

            public NoNestingNamedColumns(String someString) {
                this.someString = someString;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                NoNestingNamedColumns noNestingNamedColumns = (NoNestingNamedColumns) o;

                if (idField != noNestingNamedColumns.idField) return false;
                return Objects.equals(someString, noNestingNamedColumns.someString);
            }

            @Override
            public int hashCode() {
                int result = idField;
                result = 31 * result + (someString != null ? someString.hashCode() : 0);
                return result;
            }
        }
    }

    @DisplayName("Working with simple nested objects - no column names")
    @Nested
    class NestedObjectsNoColumnNamesTest {
        private static final Class<Employee> EMPLOYEE_CLASS = Employee.class;
        private static final String EMPLOYEE_CLASS_NAME = EMPLOYEE_CLASS.getSimpleName();
        private static final String EMPLOYEE_CLASS_NAME_LOWERCASE = EMPLOYEE_CLASS_NAME.toLowerCase();

        private static final String EMPLOYEE_ID = "employeeId";
        private static final String EMPLOYEE_NAME = "employeeName";
        private static final String SOME_EMPLOYEE_NAME = "someName";
        private static final int SOME_EMPLOYEE_ID = 1;
        private static final String SELECT_EMPLOYEE_ID = getSelector(EMPLOYEE_CLASS, EMPLOYEE_ID);
        private static final String EMPLOYEE_ID_ALIAS = getAlias(EMPLOYEE_CLASS, EMPLOYEE_ID);
        private static final String SELECT_EMPLOYEE_NAME = getSelector(EMPLOYEE_CLASS, EMPLOYEE_NAME);
        private static final String EMPLOYEE_NAME_ALIAS = getAlias(EMPLOYEE_CLASS, EMPLOYEE_NAME);
        private static final Class<EmployeeType> EMPLOYEE_TYPE_CLASS = EmployeeType.class;
        private static final String EMPLOYEE_TYPE_CLASS_NAME = EMPLOYEE_TYPE_CLASS.getSimpleName();
        private static final String EMPLOYEE_TYPE_CLASS_NAME_LOWERCASE = EMPLOYEE_TYPE_CLASS_NAME.toLowerCase();
        private static final String EMPLOYEE_TYPE_ID = "typeId";
        private static final String EMPLOYEE_TYPE_NAME = "typeName";
        private static final String SOME_EMPLOYEE_TYPE_NAME = "permanent";
        private static final int SOME_EMPLOYEE_TYPE_ID = 12;
        private static final String SELECT_EMPLOYEE_TYPE_ID = getSelector(EMPLOYEE_TYPE_CLASS, EMPLOYEE_TYPE_ID);
        private static final String EMPLOYEE_TYPE_ID_ALIAS = getAlias(EMPLOYEE_TYPE_CLASS, EMPLOYEE_TYPE_ID);
        private static final String SELECT_EMPLOYEE_TYPE_NAME = getSelector(EMPLOYEE_TYPE_CLASS, EMPLOYEE_TYPE_NAME);
        private static final String EMPLOYEE_TYPE_NAME_ALIAS = getAlias(EMPLOYEE_TYPE_CLASS, EMPLOYEE_TYPE_NAME);
        private static final Class<EmployeeBonus> EMPLOYEE_BONUS_CLASS = EmployeeBonus.class;
        private static final String EMPLOYEE_BONUS_CLASS_NAME = EMPLOYEE_BONUS_CLASS.getSimpleName();
        private static final String EMPLOYEE_BONUS_CLASS_NAME_LOWERCASE = EMPLOYEE_CLASS_NAME.toLowerCase();
        private static final String EMPLOYEE_BONUS_ID = "employee";
        private static final String EMPLOYEE_BONUS_AMOUNT = "amount";
        private static final int SOME_EMPLOYEE_BONUS_AMOUNT = 555;
        private static final int SOME_EMPLOYEE_BONUS_ID = 124;
        private static final String SELECT_EMPLOYEE_BONUS_ID = getSelector(EMPLOYEE_BONUS_CLASS, EMPLOYEE_BONUS_ID);
        private static final String EMPLOYEE_BONUS_ID_ALIAS = getAlias(EMPLOYEE_BONUS_CLASS, EMPLOYEE_BONUS_ID);
        private static final String SELECT_EMPLOYEE_BONUS_AMOUNT = getSelector(EMPLOYEE_BONUS_CLASS, EMPLOYEE_BONUS_AMOUNT);
        private static final String EMPLOYEE_BONUS_AMOUNT_ALIAS = getAlias(EMPLOYEE_BONUS_CLASS, EMPLOYEE_BONUS_AMOUNT);
        private MySQLService<Employee> mySqlService;

        @BeforeEach
        void setUp() {
            mySqlService = (MySQLService<Employee>) mySQLServiceProvider.provide(TypeLiteral.get(EMPLOYEE_CLASS));
        }

        @Test
        void shouldSelect_withMapFilter() throws SQLException {
            String expectedQuery = String.format(
                    "SELECT %s AS %s, %s AS %s, %s AS %s FROM %s AS %s LEFT JOIN %s AS %s ON %s = %s WHERE %s = ? AND %s = ?;",
                    SELECT_EMPLOYEE_ID,
                    EMPLOYEE_ID_ALIAS,
                    SELECT_EMPLOYEE_TYPE_ID,
                    EMPLOYEE_TYPE_ID_ALIAS,
                    SELECT_EMPLOYEE_NAME,
                    EMPLOYEE_NAME_ALIAS,
                    EMPLOYEE_CLASS_NAME,
                    EMPLOYEE_CLASS_NAME_LOWERCASE,
                    EMPLOYEE_TYPE_CLASS_NAME,
                    EMPLOYEE_TYPE_CLASS_NAME_LOWERCASE,
                    SELECT_EMPLOYEE_ID,
                    SELECT_EMPLOYEE_TYPE_ID,
                    SELECT_EMPLOYEE_ID,
                    SELECT_EMPLOYEE_NAME);

            Employee expectedEmployee = new Employee(SOME_EMPLOYEE_ID, SOME_EMPLOYEE_NAME, new EmployeeType(SOME_EMPLOYEE_TYPE_ID, SOME_EMPLOYEE_TYPE_NAME));
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(EMPLOYEE_ID_ALIAS, SOME_EMPLOYEE_ID, EMPLOYEE_NAME_ALIAS, SOME_EMPLOYEE_NAME, EMPLOYEE_TYPE_ID_ALIAS, SOME_EMPLOYEE_TYPE_ID, EMPLOYEE_TYPE_NAME_ALIAS, SOME_EMPLOYEE_TYPE_NAME)));

            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(SELECT_EMPLOYEE_ID, SOME_EMPLOYEE_ID);
            filter.put(SELECT_EMPLOYEE_NAME, SOME_EMPLOYEE_NAME);

            Employee actualEmployee = mySqlService.get(filter);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));

            assertEquals(expectedEmployee, actualEmployee);
            List<KeyVal> keyValues = actualQueryBuilder.getKeyValues();
            assertEquals(2, keyValues.size());
            assertEquals(SOME_EMPLOYEE_ID, keyValues.get(0).getValue());
            assertEquals(SOME_EMPLOYEE_NAME, keyValues.get(SOME_EMPLOYEE_ID).getValue());
        }

        @Test
        void shouldSelectAll() throws SQLException {
            String someOtherEmployeeName = SOME_EMPLOYEE_NAME + 2;
            String someOtherEmployeeTypeName = SOME_EMPLOYEE_TYPE_NAME + 2;
            int someOtherEmployeeId = SOME_EMPLOYEE_ID + 2;
            int someOtherEmployeeTypeId = SOME_EMPLOYEE_TYPE_ID + 2;
            String expectedQuery = String.format(
                    "SELECT %s AS %s, %s AS %s, %s AS %s FROM %s AS %s LEFT JOIN %s AS %s ON %s = %s;",
                    SELECT_EMPLOYEE_ID,
                    EMPLOYEE_ID_ALIAS,
                    SELECT_EMPLOYEE_TYPE_ID,
                    EMPLOYEE_TYPE_ID_ALIAS,
                    SELECT_EMPLOYEE_NAME,
                    EMPLOYEE_NAME_ALIAS,
                    EMPLOYEE_CLASS_NAME,
                    EMPLOYEE_CLASS_NAME_LOWERCASE,
                    EMPLOYEE_TYPE_CLASS_NAME,
                    EMPLOYEE_TYPE_CLASS_NAME_LOWERCASE,
                    SELECT_EMPLOYEE_ID,
                    SELECT_EMPLOYEE_TYPE_ID);

            Employee expectedEmployee = new Employee(SOME_EMPLOYEE_ID, SOME_EMPLOYEE_NAME, new EmployeeType(SOME_EMPLOYEE_TYPE_ID, SOME_EMPLOYEE_TYPE_NAME));
            Employee expectedEmployee1 = new Employee(someOtherEmployeeId, someOtherEmployeeName, new EmployeeType(someOtherEmployeeTypeId, someOtherEmployeeTypeName));

            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(
                    List.of(
                            Map.of(EMPLOYEE_ID_ALIAS, SOME_EMPLOYEE_ID, EMPLOYEE_NAME_ALIAS, SOME_EMPLOYEE_NAME, EMPLOYEE_TYPE_ID_ALIAS, SOME_EMPLOYEE_TYPE_ID, EMPLOYEE_TYPE_NAME_ALIAS, SOME_EMPLOYEE_TYPE_NAME),
                            Map.of(EMPLOYEE_ID_ALIAS, someOtherEmployeeId, EMPLOYEE_NAME_ALIAS, someOtherEmployeeName, EMPLOYEE_TYPE_ID_ALIAS, someOtherEmployeeTypeId, EMPLOYEE_TYPE_NAME_ALIAS, someOtherEmployeeTypeName)
                    )
            );

            List<Employee> actualEmployees = mySqlService.getAll();

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
            assertEquals(List.of(expectedEmployee, expectedEmployee1), actualEmployees);
        }

        @Test
        void shouldSelectAll_withMapFilter() throws SQLException {
            String expectedQuery = String.format(
                    "SELECT %s AS %s, %s AS %s, %s AS %s FROM %s AS %s LEFT JOIN %s AS %s ON %s = %s WHERE %s = ? AND %s = ?;",
                    SELECT_EMPLOYEE_ID,
                    EMPLOYEE_ID_ALIAS,
                    SELECT_EMPLOYEE_TYPE_ID,
                    EMPLOYEE_TYPE_ID_ALIAS,
                    SELECT_EMPLOYEE_NAME,
                    EMPLOYEE_NAME_ALIAS,
                    EMPLOYEE_CLASS_NAME,
                    EMPLOYEE_CLASS_NAME_LOWERCASE,
                    EMPLOYEE_TYPE_CLASS_NAME,
                    EMPLOYEE_TYPE_CLASS_NAME_LOWERCASE,
                    SELECT_EMPLOYEE_ID,
                    SELECT_EMPLOYEE_TYPE_ID,
                    SELECT_EMPLOYEE_ID,
                    SELECT_EMPLOYEE_NAME);

            Employee expectedEmployee = new Employee(SOME_EMPLOYEE_ID, SOME_EMPLOYEE_NAME, new EmployeeType(SOME_EMPLOYEE_TYPE_ID, SOME_EMPLOYEE_TYPE_NAME));
            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(List.of(Map.of(EMPLOYEE_ID_ALIAS, SOME_EMPLOYEE_ID, EMPLOYEE_NAME_ALIAS, SOME_EMPLOYEE_NAME, EMPLOYEE_TYPE_ID_ALIAS, SOME_EMPLOYEE_TYPE_ID, EMPLOYEE_TYPE_NAME_ALIAS, SOME_EMPLOYEE_TYPE_NAME)));

            LinkedHashMap<String, Object> filter = new LinkedHashMap<>();
            filter.put(SELECT_EMPLOYEE_ID, SOME_EMPLOYEE_ID);
            filter.put(SELECT_EMPLOYEE_NAME, SOME_EMPLOYEE_NAME);

            List<Employee> actualEmployee = mySqlService.getAll(filter);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));

            assertEquals(List.of(expectedEmployee), actualEmployee);
            List<KeyVal> keyValues = actualQueryBuilder.getKeyValues();
            assertEquals(2, keyValues.size());
            assertEquals(SOME_EMPLOYEE_ID, keyValues.get(0).getValue());
            assertEquals(SOME_EMPLOYEE_NAME, keyValues.get(SOME_EMPLOYEE_ID).getValue());
        }

        @Test
        void shouldSave_andSetGeneratedId() throws SQLException {
            String employeeTypeIdColumn = "employee_type_id";
            String expectedQuery = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?);", EMPLOYEE_CLASS_NAME, EMPLOYEE_NAME, employeeTypeIdColumn);
            Employee employee = new Employee(SOME_EMPLOYEE_ID, SOME_EMPLOYEE_NAME, new EmployeeType(SOME_EMPLOYEE_TYPE_ID, SOME_EMPLOYEE_TYPE_NAME));
            when(mockMySqlClient.executeInsertQuery(any())).thenReturn(SOME_EMPLOYEE_ID);

            mySqlService.save(employee);

            verify(mockMySqlClient).executeInsertQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(1, queryParts.size());
            Insert insertQueryPart = (Insert) queryParts.get(0);
            assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, EMPLOYEE_NAME, SOME_EMPLOYEE_NAME, 1), new MySqlValue(MysqlType.INT, employeeTypeIdColumn, SOME_EMPLOYEE_TYPE_ID, 2)), insertQueryPart.getValuesForParams());
            assertEquals(SOME_EMPLOYEE_ID, employee.employeeId);
        }

        @Test
        void shouldUpdate() throws SQLException {
            String employeeTypeIdColumn = "employee_type_id";
            String expectedQuery = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?;", EMPLOYEE_CLASS_NAME, EMPLOYEE_NAME, employeeTypeIdColumn, EMPLOYEE_ID);
            Employee employee = new Employee(SOME_EMPLOYEE_ID, SOME_EMPLOYEE_NAME, new EmployeeType(SOME_EMPLOYEE_TYPE_ID, SOME_EMPLOYEE_TYPE_NAME));
            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);

            mySqlService.update(employee);

            verify(mockMySqlClient).executeOtherDmlQuery(queryBuilderCaptor.capture());
            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
            assertEquals(3, queryParts.size());
            Update updateQueryPart = (Update) queryParts.get(0);
            assertEquals(List.of(new MySqlValue(MysqlType.VARCHAR, EMPLOYEE_NAME, SOME_EMPLOYEE_NAME, 1), new MySqlValue(MysqlType.INT, employeeTypeIdColumn, SOME_EMPLOYEE_TYPE_ID, 2)), updateQueryPart.getValuesForParams());
            KeyVal keyVal = (KeyVal) queryParts.get(2);
            assertEquals(SOME_EMPLOYEE_ID, keyVal.getValue());
        }
//
//        @Test
//        void shouldDelete() throws SQLException {
//            String expectedQuery = String.format("DELETE FROM %s AS %s WHERE %s = ?;", EMPLOYEE_CLASS_NAME, EMPLOYEE_CLASS_NAME_LOWERCASE, EMPLOYEE_ID);
//            Employee employee = new Employee(SOME_EMPLOYEE_ID, SOME_EMPLOYEE_NAME);
//            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);
//
//            mySqlService.delete(employee);
//
//            verify(mockMySqlClient).executeOtherDmlQuery(queryBuilderCaptor.capture());
//            QueryBuilder actualQueryBuilder = queryBuilderCaptor.getValue();
//            assertEquals(expectedQuery, getActualQuery(actualQueryBuilder));
//            List<QueryPart> queryParts = actualQueryBuilder.getQueryParts();
//            assertEquals(4, queryParts.size());
//            KeyVal keyVal = (KeyVal) queryParts.get(3);
//            assertEquals(SOME_EMPLOYEE_ID, keyVal.getValue());
//        }

        private static class EmployeeType {
            @MySqlField(type = MysqlType.INT, primary = true)
            private int typeId;

            @MySqlField(type = MysqlType.VARCHAR)
            private String typeName;

            public EmployeeType() {
            }

            public EmployeeType(int typeId, String typeName) {
                this.typeId = typeId;
                this.typeName = typeName;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                EmployeeType that = (EmployeeType) o;

                if (typeId != that.typeId) return false;
                return Objects.equals(typeName, that.typeName);
            }

            @Override
            public int hashCode() {
                int result = typeId;
                result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
                return result;
            }
        }

        private static class Employee {
            @MySqlField(type = MysqlType.INT, primary = true)
            private int employeeId;

            @MySqlField(type = MysqlType.VARCHAR)
            private String employeeName;

            @MySqlField(type = MysqlType.INT, column = "employee_type_id")
            @com.eu.atit.pantheon.annotation.data.Nested(inward = true)
            private EmployeeType employeeType;

            public Employee(int employeeId, String employeeName, EmployeeType employeeType) {
                this.employeeId = employeeId;
                this.employeeName = employeeName;
                this.employeeType = employeeType;
            }

            public Employee() {
            }

            public Employee(String employeeName) {
                this.employeeName = employeeName;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Employee employee = (Employee) o;

                if (employeeId != employee.employeeId) return false;
                if (!Objects.equals(employeeName, employee.employeeName))
                    return false;
                return Objects.equals(employeeType, employee.employeeType);
            }

            @Override
            public int hashCode() {
                int result = employeeId;
                result = 31 * result + (employeeName != null ? employeeName.hashCode() : 0);
                result = 31 * result + (employeeType != null ? employeeType.hashCode() : 0);
                return result;
            }

            @Override
            public String toString() {
                return "Employee{" +
                       "employeeId=" + employeeId +
                       ", employeeName='" + employeeName + '\'' +
                       ", employeeType=" + employeeType +
                       '}';
            }
        }

        private static class EmployeeBonus {
            @MySqlField(type = MysqlType.INT, primary = true)
            private Employee employee;
            @MySqlField(type = MysqlType.INT)
            private int amount;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                EmployeeBonus that = (EmployeeBonus) o;

                if (amount != that.amount) return false;
                return Objects.equals(employee, that.employee);
            }

            @Override
            public int hashCode() {
                int result = employee != null ? employee.hashCode() : 0;
                result = 31 * result + amount;
                return result;
            }
        }
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
