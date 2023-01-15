//package com.eu.atit.mysql.service;
//
//import com.eu.atit.mysql.client.MySqlClient;
//import com.eu.atit.mysql.query.MySqlValue;
//import com.eu.atit.mysql.query.QueryBuilder;
//import com.google.inject.TypeLiteral;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.lang.reflect.Field;
//import java.sql.SQLException;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MySQLServiceTest {
//    private static final Class<Object> SOME_CLASS = Object.class;
//    private static final String SOME_TABLE = "someTable";
//    private static final String SOME_VAR = "someVar";
//    private static final String SOME_OTHER_VAR = "someOtherVar";
//
//    @Mock
//    private MySQLServiceFieldsProvider mockMySQLServiceFieldsProvider;
//
//    @Mock
//    private MySqlClient mockMySqlClient;
//
//    @Mock
//    private QueryBuilder mockQueryBuilder;
//
//    @Mock
//    private Instantiator<Object> mockInstantiator;
//
//    @Mock
//    private FieldValueSetter<Object> mockFieldValueSetter;
//
//    @Mock
//    private SpecificFieldValueSetter<Object> mockSpecificFieldValueSetter;
//
//    @Mock
//    private JoinInfo mockJoinInfo;
//
//    @Mock
//    private MySqlValue mockMySqlValue;
//
//    @Mock
//    private FieldMySqlValue<Object> mockFieldMySqlValue;
//
//    @Mock
//    private Object mockObject;
//
//    private LinkedList<SpecificFieldValueSetter<Object>> someSpecificFieldValueSetters;
//    private List<JoinInfo> someJoinInfos;
//
//    @BeforeEach
//    void setUp() {
//        someSpecificFieldValueSetters = new LinkedList<>(List.of(mockSpecificFieldValueSetter, mockSpecificFieldValueSetter));
//        someJoinInfos = List.of(mockJoinInfo, mockJoinInfo);
//
//        when(mockMySQLServiceFieldsProvider.getTableName(SOME_CLASS)).thenReturn(SOME_TABLE);
//        when(mockMySQLServiceFieldsProvider.getInstantiator(any())).thenReturn(mockInstantiator);
//        when(mockMySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(any())).thenReturn(mockFieldMySqlValue);
//        when(mockMySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(any())).thenReturn(mockFieldValueSetter);
//        when(mockMySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(any())).thenReturn(Map.of(SOME_VAR, mockFieldValueSetter));
//
//        when(mockMySQLServiceFieldsProvider.getSpecificFieldValueSetters(any())).thenReturn(someSpecificFieldValueSetters);
//        when(mockMySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(any())).thenReturn(new LinkedList<>(List.of(mockFieldMySqlValue, mockFieldMySqlValue)));
//        when(mockFieldMySqlValue.alias()).thenReturn(SOME_VAR).thenReturn(SOME_OTHER_VAR);
//    }
//
//    @Test
//    void shouldInitializeViaTheProvider() {
//        when(mockMySQLServiceFieldsProvider.getSpecificFieldValueSetters(any())).thenReturn(someSpecificFieldValueSetters);
//        when(mockMySQLServiceFieldsProvider.getJoinInfos(any())).thenReturn(someJoinInfos);
//
//        MySQLService<Object> mySQLService = mySQLService();
//
//        verify(mockMySQLServiceFieldsProvider).validateClass(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getNonPrimaryKeyFieldMySqlValues(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getSpecificFieldValueSetters(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getInstantiator(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getPrimaryKeyFieldValueSetter(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getPrimaryKeyFieldMySqlValue(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getTableName(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getNonPrimaryFieldValueSetterMap(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getColumnsAndAliases(SOME_TABLE.toLowerCase(), someSpecificFieldValueSetters, someJoinInfos);
//        verify(mockMySQLServiceFieldsProvider).getSpecificNestedFieldValueSetters(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getJoinInfos(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getSpecificFieldValueOverrides(SOME_CLASS);
//        verify(mockMySQLServiceFieldsProvider).getPrimaryKeyValueSetter(SOME_CLASS);
//        verifyNoMoreInteractions(mockMySQLServiceFieldsProvider);
//
//        Map<String, FieldMySqlValue<Object>> fieldMySqlValueMap = mySQLService.getFieldMySqlValueMap();
//        Map<String, FieldMySqlValue<Object>> expectedFieldMySqlValueMap = Map.of(SOME_VAR, mockFieldMySqlValue, SOME_OTHER_VAR, mockFieldMySqlValue);
//        Assertions.assertEquals(expectedFieldMySqlValueMap, fieldMySqlValueMap);
//    }
//
//    private MySQLService<Object> mySQLService() {
//        MySQLService<Object> objectMySQLService = new MySQLService<>(mockMySqlClient, TypeLiteral.get(SOME_CLASS));
//        objectMySQLService.init(mockMySQLServiceFieldsProvider);
//        return objectMySQLService;
//    }
//
//    @DisplayName("Filtered selection query builder")
//    @Nested
//    class FilteredSelect {
//        @Test
//        void shouldReturnQueryBuilderFilteredByTableName() {
//            MySQLService<Object> mySQLService = mySQLService();
//
//            QueryBuilder expectedQueryBuilder = new QueryBuilder();
//            expectedQueryBuilder.select(mySQLService.columnsAndAliases());
//            expectedQueryBuilder.from(SOME_TABLE);
//
//            Assertions.assertEquals(expectedQueryBuilder, mySQLService.filteredSelect());
//        }
//    }
//
//    @DisplayName("Get single element")
//    @Nested
//    class Get {
//        @Test
//        void shouldReturnInstantiatedAndPopulatedObject() throws SQLException {
//            final List<Map<String, Object>> someSingleRowResult = List.of(Map.of("SomeString", "someVal"));
//
//            when(mockInstantiator.get()).thenReturn(mockObject);
//            when(mockMySqlClient.executeSelectQuery(mockQueryBuilder)).thenReturn(someSingleRowResult);
//
//            mySQLService().get(mockQueryBuilder);
//
//            verify(mockInstantiator).get();
//            verify(mockSpecificFieldValueSetter, times(someSpecificFieldValueSetters.size())).accept(mockObject, someSingleRowResult.get(0));
//        }
//
//        @Test
//        void shouldThrowExceptionWhenSelectReturnsNoElements() throws SQLException {
//            List<Map<String, Object>> noRowsResult = List.of();
//            when(mockMySqlClient.executeSelectQuery(mockQueryBuilder)).thenReturn(noRowsResult);
//
//            Assertions.assertThrows(IllegalStateException.class, () -> mySQLService().get(mockQueryBuilder));
//
//            verifyNoInteractions(mockInstantiator);
//            verifyNoInteractions(mockSpecificFieldValueSetter);
//        }
//
//        @Test
//        void shouldThrowExceptionWhenSelectReturnsMoreThanOneElement() throws SQLException {
//            List<Map<String, Object>> multipleRows = List.of(Map.of(), Map.of());
//            when(mockMySqlClient.executeSelectQuery(mockQueryBuilder)).thenReturn(multipleRows);
//
//            Assertions.assertThrows(IllegalStateException.class, () -> mySQLService().get(mockQueryBuilder));
//
//            verifyNoInteractions(mockInstantiator);
//            verifyNoInteractions(mockSpecificFieldValueSetter);
//        }
//    }
//
//    @DisplayName("Get one or all elements using a filter")
//    @Nested
//    class FilteredGet {
//        @Test
//        void get_shouldUseProvidedFilterToMapToMySqlValuesAndIgnoreOnesThatDoNotMap() throws SQLException {
//            MySQLService<Object> spy = spy(mySQLService());
//            int intVal = 1;
//            String stringVal = "2";
//            Map<String, Object> filter = Map.of(SOME_VAR, intVal, SOME_OTHER_VAR, stringVal, "someNotExisting", 1);
//
//            when(mockFieldMySqlValue.of(intVal)).thenReturn(mockMySqlValue);
//            when(mockFieldMySqlValue.of(stringVal)).thenReturn(mockMySqlValue);
//            doReturn(mockObject).when(spy).get(any(QueryBuilder.class));
//
//            QueryBuilder expected = spy.filteredSelect();
//            expected.where();
//            expected.keyIsVal(mockMySqlValue);
//            expected.and();
//            expected.keyIsVal(mockMySqlValue);
//
//            spy.get(filter);
//
//            verify(spy).get(expected);
//        }
//
//        @Test
//        void get_shouldThrowExceptionWhenAllFiltersDoNotMatchMySqlValues() {
//            Map<String, Object> filter = Map.of("someNotExisting", 1);
//
//            Assertions.assertThrows(IllegalStateException.class, () -> mySQLService().get(filter));
//        }
//
//        @Test
//        void getAll_shouldUseProvidedFilterToMapToMySqlValuesAndIgnoreOnesThatDoNotMap() throws SQLException {
//            MySQLService<Object> spy = spy(mySQLService());
//            int intVal = 1;
//            String stringVal = "2";
//            Map<String, Object> filter = Map.of(SOME_VAR, intVal, SOME_OTHER_VAR, stringVal, "someNotExisting", 1);
//
//            when(mockFieldMySqlValue.of(intVal)).thenReturn(mockMySqlValue);
//            when(mockFieldMySqlValue.of(stringVal)).thenReturn(mockMySqlValue);
//            doReturn(List.of(mockObject)).when(spy).getAll(any(QueryBuilder.class));
//
//            QueryBuilder expected = spy.filteredSelect();
//            expected.where();
//            expected.keyIsVal(mockMySqlValue);
//            expected.and();
//            expected.keyIsVal(mockMySqlValue);
//
//            spy.getAll(filter);
//
//            verify(spy).getAll(expected);
//        }
//
//        @Test
//        void getAll_shouldThrowExceptionWhenAllFiltersDoNotMatchMySqlValues() {
//            Map<String, Object> filter = Map.of("someNotExisting", 1);
//
//            Assertions.assertThrows(IllegalStateException.class, () -> mySQLService().getAll(filter));
//        }
//    }
//
//    @DisplayName("Get all elements")
//    @Nested
//    class GetAll {
//        @Test
//        void shouldUseFilteredSelectToFetchAllElements() throws SQLException, IllegalAccessException {
//            List<Map<String, Object>> multipleRows = List.of(Map.of(), Map.of());
//
//            int expectedNumberOfElements = 2;
//            int expectedNumberOfSetterOperations = expectedNumberOfElements * someSpecificFieldValueSetters.size();
//
//            when(mockInstantiator.get()).thenReturn(mockObject).thenReturn(mockObject);
//            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(multipleRows);
//            when(mockFieldValueSetter.getFieldValue(mockObject)).thenReturn(1).thenReturn(2);
//
//            MySQLService<Object> mySQLService = mySQLService();
//
//            mySQLService.getAll();
//
//            verify(mockMySqlClient).executeSelectQuery(mySQLService.filteredSelect());
//            verify(mockInstantiator, times(expectedNumberOfElements)).get();
//            verify(mockSpecificFieldValueSetter, times(expectedNumberOfSetterOperations)).accept(eq(mockObject), any());
//        }
//
//        @Test
//        void shouldFetchAllElements() throws SQLException, IllegalAccessException {
//            List<Map<String, Object>> multipleRows = List.of(Map.of(), Map.of());
//            int expectedNumberOfElements = 2;
//            int expectedNumberOfSetterOperations = expectedNumberOfElements * someSpecificFieldValueSetters.size();
//
//            when(mockInstantiator.get()).thenReturn(mockObject).thenReturn(mockObject);
//            when(mockMySqlClient.executeSelectQuery(mockQueryBuilder)).thenReturn(multipleRows);
//            when(mockFieldValueSetter.getFieldValue(mockObject)).thenReturn(1).thenReturn(2);
//            mySQLService().getAll(mockQueryBuilder);
//
//            verify(mockMySqlClient).executeSelectQuery(mockQueryBuilder);
//            verify(mockInstantiator, times(expectedNumberOfElements)).get();
//            verify(mockSpecificFieldValueSetter, times(expectedNumberOfSetterOperations)).accept(eq(mockObject), any());
//        }
//
//    }
//
//    @DisplayName("Delete an element")
//    @Nested
//    class Delete {
//        @Test
//        void shouldDeleteSpecifiedElement() throws SQLException {
//            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue);
//            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);
//
//            QueryBuilder expectedQueryBuilder = new QueryBuilder();
//            expectedQueryBuilder.delete();
//            expectedQueryBuilder.from(SOME_TABLE);
//            expectedQueryBuilder.where();
//            expectedQueryBuilder.keyIsVal(mockMySqlValue);
//
//            mySQLService().delete(mockObject);
//
//            verify(mockMySqlClient).executeOtherDmlQuery(expectedQueryBuilder);
//        }
//
//        @Test
//        void shouldThrowExceptionWhenNothingIsDeleted() throws SQLException {
//            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue);
//            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(0);
//
//            Assertions.assertThrows(RuntimeException.class, () -> mySQLService().delete(mockObject));
//        }
//
//    }
//
//    @DisplayName("Save an element")
//    @Nested
//    class Save {
//        @Test
//        void shouldSaveAnElement() throws SQLException {
//            int someInsertId = 1;
//            when(mockMySqlClient.executeInsertQuery(any())).thenReturn(someInsertId);
//            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue);
//
//            LinkedList<MySqlValue> expectedMySqlValues = new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue));
//            QueryBuilder expectedQueryBuilder = new QueryBuilder();
//            expectedQueryBuilder.insert(SOME_TABLE, expectedMySqlValues);
//
//            mySQLService().save(mockObject);
//            verify(mockMySqlClient).executeInsertQuery(expectedQueryBuilder);
//        }
//    }
//
//    @DisplayName("Update an element")
//    @Nested
//    class Update {
//        @Test
//        void shouldUpdateAnElement() throws SQLException {
//            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue);
//            LinkedList<MySqlValue> expectedMySqlValues = new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue));
//            QueryBuilder expectedQueryBuilder = new QueryBuilder();
//            expectedQueryBuilder.update(SOME_TABLE, expectedMySqlValues);
//            expectedQueryBuilder.where();
//            expectedQueryBuilder.keyIsVal(mockMySqlValue);
//
//            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(1);
//
//            mySQLService().update(mockObject);
//            verify(mockMySqlClient).executeOtherDmlQuery(expectedQueryBuilder);
//        }
//
//        @Test
//        void shouldThrowExceptionWhenNoElementUpdated() throws SQLException {
//            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue);
//            when(mockMySqlClient.executeOtherDmlQuery(any())).thenReturn(0);
//
//            Assertions.assertThrows(RuntimeException.class, () -> mySQLService().update(mockObject));
//        }
//
//    }
//
//    @DisplayName("Instantiation of an element")
//    @Nested
//    class Instantiate {
//        @Test
//        void shouldInstantiateAnObjectWithMappedValuesAndIgnoringUnmappedValues() {
//            when(mockInstantiator.get()).thenReturn(mockObject);
//            int expectedValue = 1;
//            mySQLService().instanceOfT(Map.of(SOME_VAR, expectedValue, "unmapped", "2"));
//
//            verify(mockFieldValueSetter).accept(mockObject, expectedValue);
//            verifyNoMoreInteractions(mockFieldValueSetter);
//        }
//    }
//}