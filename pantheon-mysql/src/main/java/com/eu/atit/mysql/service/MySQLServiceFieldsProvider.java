package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.google.inject.TypeLiteral;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

class MySQLServiceFieldsProvider {
    static final String NO_PRIMARY_KEY_FOUND = "No primary key found";
    static final String THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY = "There can be only one primary key";
    static final String FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR = "Failed to locate an empty constructor";
    static final String NESTING_DIRECTION_NEEDS_TO_BE_SINGULAR = "Nesting direction needs to be singular";
    private final MySQLServiceProvider mySQLServiceProvider;

    public MySQLServiceFieldsProvider(MySQLServiceProvider mySQLServiceProvider) {
        this.mySQLServiceProvider = mySQLServiceProvider;
    }

    <T> String getTableName(Class<T> tClass) {
        return tClass.getSimpleName();
    }

    <T> String getTableNameLowercase(Class<T> tClass) {
        return getTableName(tClass).toLowerCase();
    }

    <T> Instantiator<T> getInstantiator(Class<T> tClass) {
        try {
            Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return new Instantiator<>(declaredConstructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, e);
        }
    }

    <T> FieldValueGetter<T> getPrimaryKeyFieldValueGetter(Class<T> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        field.setAccessible(true);
        return new FieldValueGetter<>(field);
    }

    <T> FieldValueSetter<T> getPrimaryKeyFieldValueSetter(Class<T> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        field.setAccessible(true);
        return new FieldValueSetter<>(field);
    }

    <T> Map<String, FieldValueSetter<T>> getNonPrimaryFieldValueSetterMap(Class<T> tClass) {
        Map<String, FieldValueSetter<T>> nonPrimaryFieldValueSetterMap = new HashMap<>();

        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo != null) {
                if (!mySqlFieldInfo.primary()) {
                    nonPrimaryFieldValueSetterMap.put(field.getName(), new FieldValueSetter<>(field));
                }
            } else {
                nonPrimaryFieldValueSetterMap.put(field.getName(), new FieldValueSetter<>(field));
            }
        }

        return nonPrimaryFieldValueSetterMap;
    }

    <T> SpecificFieldValueSetter<T> getPrimaryKeyValueSetter(Class<T> tClass) {
        String tableName = getTableNameLowercase(tClass);
        Field field = getDeclaredPrimaryField(tClass);
        field.setAccessible(true);

        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return new SpecificFieldValueSetter<>(field, tableName);
        } else {
            return new SpecificFieldValueSetter<>(field, fieldName, tableName);
        }
    }

    <T> List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters(Class<T> tClass) {
        List<SpecificFieldValueSetter<T>> setters = new ArrayList<>();
        String tableName = getTableNameLowercase(tClass);
        for (Field field : getDeclaredSqlFields(tClass)) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            String fieldName = mySqlFieldInfo.column();
            if (fieldName.isBlank()) {
                setters.add(new SpecificFieldValueSetter<>(field, tableName));
            } else {
                setters.add(new SpecificFieldValueSetter<>(field, fieldName, tableName));
            }
        }

        return setters;
    }

    <T> FieldMySqlValue<T> getPrimaryKeyFieldMySqlValue(Class<T> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        field.setAccessible(true);

        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        String fieldName = mySqlFieldInfo.column();
        String tableName = getTableNameLowercase(tClass);
        if (fieldName.isBlank()) {
            return new FieldMySqlValue<>(field, mySqlFieldInfo.type(), tableName);
        } else {
            return new FieldMySqlValue<>(field, mySqlFieldInfo.type(), fieldName, tableName);
        }
    }

    <T> List<FieldMySqlValue<T>> getNonPrimaryKeyFieldMySqlValues(Class<T> tClass) {
        List<FieldMySqlValue<T>> getters = new ArrayList<>();

        for (Field field : getDeclaredSqlFields(tClass)) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
            String tableName = getTableNameLowercase(tClass);
            if (!mySqlFieldInfo.primary()) {
                String fieldName = mySqlFieldInfo.column();
                if (fieldName.isBlank()) {
                    getters.add(new FieldMySqlValue<>(field, mySqlFieldInfo.type(), tableName));
                } else {
                    getters.add(new FieldMySqlValue<>(field, mySqlFieldInfo.type(), fieldName, tableName));
                }
            }
        }

        return getters;
    }

    <T> List<SpecificFieldValueOverride<T>> getSpecificFieldValueOverrides(Class<T> tClass) {
        List<SpecificFieldValueOverride<T>> overrides = new ArrayList<>();
        for (Field field : getDeclaredSqlFields(tClass)) {
            field.setAccessible(true);
            overrides.add(new SpecificFieldValueOverride<>(field));
        }

        for (Field field : getDeclaredNestedFields(tClass)) {
            field.setAccessible(true);
            overrides.add(new SpecificFieldValueOverride<>(field));
        }

        return overrides;
    }

    <T> List<SpecificNestedFieldValueSetter<T>> getSpecificNestedFieldValueSetters(Class<T> tClass) {
        List<SpecificNestedFieldValueSetter<T>> setters = new ArrayList<>();
        for (Field field : getDeclaredNestedFields(tClass)) {
            field.setAccessible(true);
            Type genericType = field.getGenericType();
            if (isList(genericType)) {
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                setters.add(new SpecificNestedListFieldValueSetter<>(field, mySQLServiceProvider.provideMySqlServiceNoCache(TypeLiteral.get(actualTypeArgument))));
            } else {
                setters.add(new SpecificNestedFieldValueSetter<>(field, mySQLServiceProvider.provideMySqlServiceNoCache(TypeLiteral.get(field.getType()))));
            }
        }

        return setters;
    }

    <T> List<JoinInfo> getJoinInfos(Class<T> modelClass) {
        List<JoinInfo> joinInfos = new ArrayList<>();

        for (Field field : getDeclaredNestedFields(modelClass)) {
            Nested nestingInfo = field.getAnnotation(Nested.class);
            Type genericType = field.getGenericType();
            boolean isList = isList(genericType);
            validateNestingDirection(nestingInfo, isList);

            MySQLModelDescriptor<?> nestedMySQLModelDescriptor = mySQLServiceProvider.provideMySqlModelDescriptorNoCache(TypeLiteral.get(joiningWith(field, genericType, isList)));
            String modelClassName = modelClass.getSimpleName();
            String modelClassNameLowerCase = modelClassName.toLowerCase();
            String targetTableName = nestedMySQLModelDescriptor.getTableName();
            String targetTableLowercase = nestedMySQLModelDescriptor.getTableNameLowercase();
            String nestedPrimaryKeyFieldName = nestedMySQLModelDescriptor.getPrimaryKeyFieldMySqlValue().getFieldName();
            List<ColumnNameAndAlias> columnNameAndAliases = getColumnNameAndAliases(nestingInfo, nestedMySQLModelDescriptor, targetTableLowercase);

            if (!isList) {
                String link = nestingInfo.link();
                if (nestingInfo.outward()) {
                    joinInfos.add(new JoinInfo(targetTableName, targetTableLowercase, nestedPrimaryKeyFieldName, modelClassNameLowerCase, getOutwardJoinForeignKey(link, field, nestedMySQLModelDescriptor), columnNameAndAliases));
                }

                if (nestingInfo.inward()) {
                    joinInfos.add(new JoinInfo(targetTableName, targetTableLowercase, getInwardJoinForeignKey(link, modelClass), modelClassNameLowerCase, nestedPrimaryKeyFieldName, columnNameAndAliases));
                }
            } else {
                Class<T> actualTypeArgument = (Class<T>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                String connectingTable = nestingInfo.connection().isEmpty() ? connectingTable(getTableName(modelClass), targetTableName) : nestingInfo.connection();
                String connectingTableLowercase = connectingTable.toLowerCase();
                String foreignKeyFrom = nestingInfo.from().isEmpty() ? connectingTable(actualTypeArgument.getSimpleName().toLowerCase(), nestedPrimaryKeyFieldName) : nestingInfo.from();
                String foreignKeyTo = nestingInfo.to().isEmpty() ? connectingTable(modelClassNameLowerCase, getPrimaryKeyFieldMySqlValue(modelClass).getFieldName()) : nestingInfo.to();

                JoinInfo joinToConnectingTable = new JoinInfo(connectingTable, connectingTableLowercase, foreignKeyFrom, modelClassNameLowerCase, nestedPrimaryKeyFieldName, columnNameAndAliases, true);
                joinInfos.add(joinToConnectingTable);
                JoinInfo joinFromConnectingTableToTarget = new JoinInfo(targetTableName, targetTableLowercase, nestedPrimaryKeyFieldName, connectingTableLowercase, foreignKeyTo, columnNameAndAliases, true);
                joinInfos.add(joinFromConnectingTableToTarget);
            }

            List<JoinInfo> nestedJoins = nestedMySQLModelDescriptor.getJoinInfos();
            if (nestedJoins != null)
                joinInfos.addAll(nestedJoins);
        }


        return joinInfos;
    }

    private String connectingTable(String modelClass, String targetTableLowercase) {
        return modelClass + "_" + targetTableLowercase;
    }

    private String getInwardJoinForeignKey(String link, Class<?> tClass) {
        if (link.isBlank()) {
            return connectingTable(tClass.getSimpleName().toLowerCase(), getPrimaryKeyFieldMySqlValue(tClass).getFieldName());
        } else {
            return link;
        }
    }

    private String getOutwardJoinForeignKey(String link, Field field, MySQLModelDescriptor<?> nestedMySQLModelDescriptor) {
        if (link.isBlank()) {
            return connectingTable(field.getType().getSimpleName().toLowerCase(), nestedMySQLModelDescriptor.getPrimaryKeyFieldMySqlValue().getFieldName());
        } else {
            return link;
        }
    }

    private static List<ColumnNameAndAlias> getColumnNameAndAliases(Nested nestingInfo, MySQLModelDescriptor<?> nestedMySQLModelDescriptor, String targetTableLowercase) {
        List<ColumnNameAndAlias> columnNameAndAliases;
        if (nestingInfo.eager()) {
            columnNameAndAliases = nestedMySQLModelDescriptor.getSpecificFieldValueSetters().stream().map(specificFieldValueSetter -> specificFieldValueSetter.fieldNameAndAlias(targetTableLowercase)).toList();
        } else {
            columnNameAndAliases = List.of(nestedMySQLModelDescriptor.getPrimaryKeyValueSetter().fieldNameAndAlias(targetTableLowercase));
        }
        return columnNameAndAliases;
    }

    private void validateNestingDirection(Nested nestingInfo, boolean isList) {
        if (!isList && nestingInfo.outward() == nestingInfo.inward()) {
            throw new RuntimeException(NESTING_DIRECTION_NEEDS_TO_BE_SINGULAR);
        }
    }

    private boolean isList(Type genericType) {
        return genericType.getTypeName().contains("List");
    }

    private Type joiningWith(Field field, Type genericType, boolean isList) {
        if (isList) {
            return ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            return field.getType();
        }
    }

    private <T> List<Field> getDeclaredSqlFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(MySqlField.class) != null).collect(Collectors.toList());
    }

    private <T> Field getDeclaredPrimaryField(Class<T> tClass) {
        List<Field> primaryKeys = Arrays.stream(tClass.getDeclaredFields()).filter(field -> {
            MySqlField annotation = field.getAnnotation(MySqlField.class);
            return annotation != null && annotation.primary();
        }).toList();

        if (primaryKeys.isEmpty())
            throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
        else if (primaryKeys.size() > 1)
            throw new RuntimeException(THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY);

        return primaryKeys.get(0);
    }

    private <T> List<Field> getDeclaredNestedFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(Nested.class) != null).collect(Collectors.toList());
    }
}
