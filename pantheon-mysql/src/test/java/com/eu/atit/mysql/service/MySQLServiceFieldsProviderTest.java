package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.eu.atit.pantheon.annotation.data.Nested;

import java.util.List;

import static com.eu.atit.mysql.service.MySQLServiceFieldsProvider.FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR;

class MySQLServiceFieldsProviderTest {
    private static final String PRIMARY_KEY_FIELD_IN_TEST_TARGET = "stringField";
    private static final String COLUMN_NAME = "column";

    private final MySQLServiceFieldsProvider mySQLServiceFieldsProvider = new MySQLServiceFieldsProvider();

    @Test
    void getJoinInfos_shouldThrowExceptionWhenInwardAndOutwardNesting() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> mySQLServiceFieldsProvider.getJoinInfos(ClassWithInvalidNesting.class));
        Assertions.assertEquals(MySQLServiceFieldsProvider.NESTING_DIRECTION_NEEDS_TO_BE_IN_ONE_DIRECTION, exception.getMessage());
    }

    @Test
    void getDeclaredPrimaryField_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> mySQLServiceFieldsProvider.getDeclaredPrimaryField(NoneArePrimary.class));
        Assertions.assertEquals(MySQLServiceFieldsProvider.NO_PRIMARY_KEY_FOUND, exception.getMessage());
    }

    @Test
    void getDeclaredPrimaryField_shouldThrowExceptionWhenMultiplePrimaryKeysFound() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> mySQLServiceFieldsProvider.getDeclaredPrimaryField(MultiPrimary.class));
        Assertions.assertEquals(MySQLServiceFieldsProvider.THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY, exception.getMessage());
    }

    @Test
    void getDeclaredPrimaryField_shouldThrowExceptionWhenPrimaryKeyIsAList() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> mySQLServiceFieldsProvider.getDeclaredPrimaryField(ListAsPrimaryKey.class));
        Assertions.assertEquals(MySQLServiceFieldsProvider.PRIMARY_KEY_CANNOT_BE_A_LIST, exception.getMessage());
    }

    
    @Test
    void getInstantiator_shouldCreateInstantiatorFromClassWithEmptyConstructor() {
        Instantiator<TestTarget> instantiator = Assertions.assertDoesNotThrow(() -> mySQLServiceFieldsProvider.getInstantiator(TestTarget.class));
        Assertions.assertNotNull(instantiator);
    }

    @Test
    void getInstantiator_shouldThrowExceptionWhenEmptyConstructorNotFound() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> mySQLServiceFieldsProvider.getInstantiator(TestTargetNoEmptyConstructor.class));
        Assertions.assertEquals(String.format(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, TestTargetNoEmptyConstructor.class), exception.getMessage());
    }

    @Test
    void getFieldMySqlValues_shouldOnlyReturnNonPrimaryFields() {
        List<FieldMySqlValue> actualFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(TestTarget.class);

        // Ensure no primary key fields are present
        Assertions.assertTrue(actualFieldMySqlValues.stream()
                .noneMatch(fieldMySqlValue -> fieldMySqlValue.getFieldName().equals(PRIMARY_KEY_FIELD_IN_TEST_TARGET)));

        // Ensure other fields are included properly
        Assertions.assertFalse(actualFieldMySqlValues.isEmpty());
    }

    @Test
    void getFieldMySqlValues_shouldOnlyReturnNonPrimaryFieldsWhenPrimaryKeyIsKnown() {
        List<FieldMySqlValue> actualFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(TestTargetKnownPrimaryKey.class);

        // Ensure no primary key fields are present
        Assertions.assertTrue(actualFieldMySqlValues.stream()
                .noneMatch(fieldMySqlValue -> fieldMySqlValue.getFieldName().equals(COLUMN_NAME)));

        // Ensure other fields are included properly
        Assertions.assertFalse(actualFieldMySqlValues.isEmpty());
    }


    @Test
    void getPrimaryKeyFieldMySqlValue_shouldProvidePrimaryKeyMySqlValue() {
        Assertions.assertEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(TestTarget.class).getFieldName());
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldProvidePrimaryKeyMySqlValueWithProvidedColumnName() {
        Assertions.assertEquals(COLUMN_NAME, mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(TestTargetNoEmptyConstructor.class).getFieldName());
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        Assertions.assertThrows(RuntimeException.class, () -> mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(NoneArePrimary.class));
    }

    @Test
    void getPrimaryKeyFieldValueSetter_shouldProvideFieldValueSetterForPrimaryKey() {
        Assertions.assertEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(TestTarget.class).getField().getName());
    }

    @Test
    void getPrimaryKeyFieldValueSetter_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        Assertions.assertThrows(RuntimeException.class, () -> mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(NoneArePrimary.class));
    }
    
    

    static class TestTarget {
        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final String stringField = "str";
        @MySqlField(type = MysqlType.INT, column = COLUMN_NAME)
        private final int intField = 1;
        @MySqlField(type = MysqlType.VARCHAR)
        private String otherStringField = "other";
        private String notAnnotated = "other";

        private TestTarget() {
        }

        TestTarget(String otherStringField) {
            this.otherStringField = otherStringField;
            notAnnotated = "";
        }
    }

    static class TestTargetNoEmptyConstructor {
        @MySqlField(type = MysqlType.VARCHAR, primary = true, column = COLUMN_NAME)
        private final int intField = 1;

        TestTargetNoEmptyConstructor(String otherStringField) {
        }
    }

    static class TestTargetKnownPrimaryKey {
        @MySqlField(type = MysqlType.INT, primary = true, column = COLUMN_NAME, known = true)
        private final int intField = 1;
        @MySqlField(type = MysqlType.VARCHAR)
        private String otherStringField = "other";

        TestTargetKnownPrimaryKey(String otherStringField) {
        }
    }

    static class MultiPrimary {
        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final int intField = 1;

        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final int intField2 = 1;
    }

    static class NoneArePrimary {
        @MySqlField(type = MysqlType.VARCHAR)
        private final int intField = 1;
    }

    static class ListAsPrimaryKey {
        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final List<String> listField = List.of("value1", "value2");
    }

    static class ClassWithInvalidNesting {
        @Nested(inward = true, outward = true)
        private final String invalidField = "invalid";
    }
}