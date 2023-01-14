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

    <T> Instantiator<T> getInstantiator(Class<T> tClass) {
        try {
            Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return new Instantiator<>(declaredConstructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, e);
        }
    }

    <T> List<FieldMySqlValue<T>> getNonPrimaryKeyFieldMySqlValues(Class<T> tClass) {
        List<FieldMySqlValue<T>> getters = new ArrayList<>();

        for (Field field : getDeclaredSqlFields(tClass)) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
            String tableName = getTableName(tClass).toLowerCase();
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

    public <T> List<JoinInfo> getJoinInfos(Class<T> tClass) {
        List<JoinInfo> joinInfos = new ArrayList<>();

        for (Field field : getDeclaredNestedFields(tClass)) {
            Nested nestingInfo = field.getAnnotation(Nested.class);
            String link = nestingInfo.link();
            MySQLService<?> nestedService;

            Type genericType = field.getGenericType();
            boolean isList = genericType.getTypeName().contains("List");
            if(isList){
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                nestedService = mySQLServiceProvider.provideNoCache(TypeLiteral.get(actualTypeArgument));
            } else {
                nestedService = mySQLServiceProvider.provideNoCache(TypeLiteral.get(field.getType()));
            }

            if (nestingInfo.connection().isEmpty() && nestingInfo.outward()==nestingInfo.inward()){
                throw new RuntimeException(NESTING_DIRECTION_NEEDS_TO_BE_SINGULAR);
            }

            String foreignKey;


            String targetTableLowercase = nestedService.getTableName().toLowerCase();
            List<ColumnNameAndAlias> columnNameAndAliases;

            if(nestingInfo.eager()){
                columnNameAndAliases = nestedService.getSpecificFieldValueSetters().stream().map(specificFieldValueSetter -> specificFieldValueSetter.fieldNameAndAlias2(targetTableLowercase)).toList();
            } else {
                columnNameAndAliases = List.of(nestedService.getPrimaryKeyValueSetter().fieldNameAndAlias2(targetTableLowercase));
            }

            if(!isList){
                if(nestingInfo.outward()){
                    if (link.isBlank()) {
                        foreignKey = field.getType().getSimpleName().toLowerCase() + "_" + nestedService.getPrimaryKeyFieldMySqlValue().getFieldName();
                    } else {
                        foreignKey = link;
                    }

                    joinInfos.add(new JoinInfo(nestedService.getTableName(), targetTableLowercase, nestedService.getPrimaryKeyFieldMySqlValue().getFieldName(), getTableName(tClass).toLowerCase(), foreignKey, columnNameAndAliases));
                }

                if (nestingInfo.inward()){
                    if (link.isBlank()) {
                        foreignKey = tClass.getSimpleName().toLowerCase() + "_" + getPrimaryKeyFieldMySqlValue(tClass).getFieldName();
                    } else {
                        foreignKey = link;
                    }

                    joinInfos.add(new JoinInfo(nestedService.getTableName(), targetTableLowercase, foreignKey, getTableName(tClass).toLowerCase(), nestedService.getPrimaryKeyFieldMySqlValue().getFieldName(), columnNameAndAliases));
                }
            } else {
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                String connection = nestingInfo.connection().isEmpty()?getTableName(tClass).toLowerCase().concat("_".concat(targetTableLowercase)):nestingInfo.connection();
                String fk = nestingInfo.from().isEmpty()?actualTypeArgument.getTypeName().toLowerCase() + "_" + nestedService.getPrimaryKeyFieldMySqlValue().getFieldName():nestingInfo.from();
                String fk2 = nestingInfo.to().isEmpty()?tClass.getSimpleName().toLowerCase() + "_" + getPrimaryKeyFieldMySqlValue(tClass).getFieldName():nestingInfo.to();

                joinInfos.add(new JoinInfo(connection, connection, fk, getTableName(tClass).toLowerCase(), nestedService.getPrimaryKeyFieldMySqlValue().getFieldName(), columnNameAndAliases));
                JoinInfo e = new JoinInfo(nestedService.getTableName(), targetTableLowercase, nestedService.getPrimaryKeyFieldMySqlValue().getFieldName(), connection, fk2, columnNameAndAliases);
                joinInfos.add(e);
            }

            List<JoinInfo> nestedJoins = nestedService.getJoinInfos();
            if (nestedJoins != null)
                joinInfos.addAll(nestedJoins);
        }


        return joinInfos;
    }

    <T> List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters(Class<T> tClass) {
        List<SpecificFieldValueSetter<T>> setters = new ArrayList<>();
        String tableName = getTableName(tClass).toLowerCase();
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

    <T> List<SpecificFieldValueOverride<T>> getSpecificFieldValueOverrides(Class<T> tClass) {
        List<SpecificFieldValueOverride<T>> overrides = new ArrayList<>();
        String tableName = getTableName(tClass).toLowerCase();
        for (Field field : getDeclaredSqlFields(tClass)) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            String fieldName = mySqlFieldInfo.column();
            if (fieldName.isBlank()) {
                overrides.add(new SpecificFieldValueOverride<>(field, tableName));
            } else {
                overrides.add(new SpecificFieldValueOverride<>(field, fieldName, tableName));
            }
        }
        for (Field field : getDeclaredNestedFields(tClass)) {
            field.setAccessible(true);
                overrides.add(new SpecificFieldValueOverride<>(field, tableName));
        }

        return overrides;
    }

    <T> SpecificFieldValueSetter<T> getPrimaryKeyValueSetter(Class<T> tClass) {
        SpecificFieldValueSetter<T> setter = null;
        String tableName = getTableName(tClass).toLowerCase();
        for (Field field : getDeclaredSqlFields(tClass)) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()){
                String fieldName = mySqlFieldInfo.column();
                if (fieldName.isBlank()) {
                    setter = (new SpecificFieldValueSetter<>(field, tableName));
                } else {
                    setter = (new SpecificFieldValueSetter<>(field, fieldName, tableName));
                }

                break;
            }
        }

        return setter;
    }

    public <T> List<SpecificNestedFieldValueSetter<T>> getSpecificNestedFieldValueSetters(Class<T> tClass) {
        List<SpecificNestedFieldValueSetter<T>> setters = new ArrayList<>();
        for (Field field : getDeclaredNestedFields(tClass)) {
            field.setAccessible(true);
            Type genericType = field.getGenericType();
            if(genericType.getTypeName().contains("List")){
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                setters.add(new SpecificNestedListFieldValueSetter<>(field, mySQLServiceProvider.provideNoCache(TypeLiteral.get(actualTypeArgument))));
            } else {
                setters.add(new SpecificNestedFieldValueSetter<>(field, mySQLServiceProvider.provideNoCache(TypeLiteral.get(field.getType()))));
            }
        }

        return setters;
    }

    public <T> List<SpecificNestedListFieldValueSetter<T>> getSpecificNestedListFieldValueSetters(Class<T> tClass) {
        List<SpecificNestedListFieldValueSetter<T>> setters = new ArrayList<>();
        for (Field field : getDeclaredNestedFields(tClass)) {
            field.setAccessible(true);
            Type genericType = field.getGenericType();
            if(genericType.getTypeName().contains("List")){
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                setters.add(new SpecificNestedListFieldValueSetter<>(field, mySQLServiceProvider.provideNoCache(TypeLiteral.get(actualTypeArgument))));
            }
        }

        return setters;
    }

    <T> FieldMySqlValue<T> getPrimaryKeyFieldMySqlValue(Class<T> tClass) {
        for (Field field : getDeclaredSqlFields(tClass)) {
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()) {
                field.setAccessible(true);

                String fieldName = mySqlFieldInfo.column();
                String tableName = getTableName(tClass).toLowerCase();
                if (fieldName.isBlank()) {
                    return new FieldMySqlValue<>(field, mySqlFieldInfo.type(), tableName);
                } else {
                    return new FieldMySqlValue<>(field, mySqlFieldInfo.type(), fieldName, tableName);
                }
            }
        }

        throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
    }

    <T> FieldValueSetter<T> getPrimaryKeyFieldValueSetter(Class<T> tClass) {
        for (Field field : getDeclaredSqlFields(tClass)) {
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()) {
                field.setAccessible(true);
                return new FieldValueSetter<>(field);
            }
        }

        throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
    }

    <T> void validateClass(Class<T> tClass) {
        try {
            tClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, e);
        }

        List<Field> primaryKeys = new ArrayList<>();
        for (Field field : getDeclaredSqlFields(tClass)) {
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()) {
                primaryKeys.add(field);
            }
        }

        if (primaryKeys.isEmpty())
            throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
        else if (primaryKeys.size() > 1)
            throw new RuntimeException(THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY);
    }

    private <T> List<Field> getDeclaredSqlFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(MySqlField.class) != null).collect(Collectors.toList());
    }

    private <T> List<Field> getDeclaredNestedFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(Nested.class) != null).collect(Collectors.toList());
    }

    <T> Map<String, FieldValueSetter<T>> getNonPrimaryFieldValueSetterMap(Class<T> tClass) {
        Map<String, FieldValueSetter<T>> map = new HashMap<>();

        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo != null) {
                if (!mySqlFieldInfo.primary()) {
                    map.put(field.getName(), new FieldValueSetter<>(field));
                }
            } else {
                map.put(field.getName(), new FieldValueSetter<>(field));
            }
        }

        return map;
    }

    public <T> ArrayList<ColumnNameAndAlias> getColumnsAndAliases(String tableName, List<SpecificFieldValueSetter<T>> specificFieldValueSetters, List<JoinInfo> joinInfos) {
        ArrayList<ColumnNameAndAlias> columnsAndAliases = new ArrayList<>();
        for (SpecificFieldValueSetter<T> specificFieldValueSetter : specificFieldValueSetters) {
            columnsAndAliases.add(specificFieldValueSetter.fieldNameAndAlias2(tableName));
        }

        for (JoinInfo joinInfo : joinInfos) {
            columnsAndAliases.addAll(joinInfo.fieldNameAndAliases());
        }

        return columnsAndAliases;
    }
}
