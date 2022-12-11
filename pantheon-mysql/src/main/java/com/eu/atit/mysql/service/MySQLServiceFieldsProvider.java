package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.google.inject.TypeLiteral;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
        List<JoinInfo> uniqueJoinInfos = new ArrayList<>();

        for (Field field : getDeclaredNestedFields(tClass)) {
            Nested nestingInfo = field.getAnnotation(Nested.class);
            String link = nestingInfo.link();
            MySQLService<?> nestedService = mySQLServiceProvider.provide(TypeLiteral.get(field.getType()));

            if (nestingInfo.outward()==nestingInfo.inward()){
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

            List<JoinInfo> nestedJoins = nestedService.getJoinInfos();
            if (nestedJoins != null)
                joinInfos.addAll(nestedJoins);


            for (int i = 0; i < joinInfos.size(); i++) {
                JoinInfo checking = joinInfos.get(i);
                boolean unique = true;
                for (int j = 1; j < joinInfos.size()-1; j++) {
                    JoinInfo against = joinInfos.get(j);
                    String checkingSource = checking.sourceTableName().toLowerCase().concat(".").concat(checking.sourceId());
                    String againstTarget = against.targetTableLowercase().concat(".").concat(against.targetId());

                    if (checkingSource.equals(againstTarget)){
                        unique = false;
                        break;
                    }
                }

                if (unique) {
                    uniqueJoinInfos.add(checking);
                }
            }

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
            setters.add(new SpecificNestedFieldValueSetter<>(field, mySQLServiceProvider.provide(TypeLiteral.get(field.getType()))));
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
