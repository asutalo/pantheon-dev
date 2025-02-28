package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.eu.atit.mysql.service.filter.MySqlValuesFilter;
import com.eu.atit.mysql.service.filter.MySqlValuesFilterWithNestedPrimaryKey;
import com.eu.atit.mysql.service.filter.NonPrimaryMySqlValuesFilter;
import com.eu.atit.mysql.service.filter.NonPrimaryNestedMySqlValuesFilter;
import com.eu.atit.mysql.service.merging.direction.Crossroads;
import com.eu.atit.mysql.service.merging.direction.DeadEnd;
import com.eu.atit.mysql.service.merging.direction.ListRoad;
import com.eu.atit.mysql.service.merging.direction.SingleRoad;
import com.eu.atit.mysql.service.merging.fields.FieldsMerger;
import com.eu.atit.mysql.service.merging.fields.FieldsMergerDTO;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.eu.atit.pantheon.helper.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

class MySQLServiceFieldsProvider {
    static final String NO_PRIMARY_KEY_FOUND = "No primary key found";
    static final String THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY = "There can be only one primary key";
    static final String FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR = "Failed to locate an empty constructor for %s";
    private static final String NESTING_DIRECTION_NEEDS_TO_BE_IN_ONE_DIRECTION = "Nesting direction needs to be in one direction";
    static final String PRIMARY_KEY_CANNOT_BE_A_LIST = "Primary key cannot be a List";

    <T> String getTableName(Class<T> tClass) {
        Table table = tClass.getAnnotation(Table.class);
        if (table == null)
            return tClass.getSimpleName();

        return table.name();
    }

    <T> String getTableNameLowercase(Class<T> tClass) {
        return getTableName(tClass).toLowerCase();
    }

    <T> Instantiator<T> getInstantiator(Class<T> tClass) { //todo turn into a full class, there's different usages of instantiator in several places
        try {
            Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return new Instantiator<>(declaredConstructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, tClass), e);
        }
    }

    <T> FieldValueSetter getPrimaryKeyFieldValueSetter(Class<T> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        field.setAccessible(true);
        return new FieldValueSetter(field);
    }

    <T> List<SpecificFieldValueSetter<T>> getSpecificFieldValueSetters(Class<T> tClass) {
        List<SpecificFieldValueSetter<T>> setters = new ArrayList<>();
        String tableName = getTableNameLowercase(tClass);
        for (Field field : getDeclaredSqlFieldsOnly(tClass)) {
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

    FieldMySqlValue getPrimaryKeyFieldMySqlValue(Class<?> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        if (field.getAnnotation(Nested.class) != null) {
            field.setAccessible(true);
            return new NestedPrimaryFieldMySqlValue(getPrimaryKeyFieldMySqlValue(field.getType()), field);
        }

        field.setAccessible(true);
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        String fieldName = mySqlFieldInfo.column();
        String tableName = getTableNameLowercase(tClass);
        if (fieldName.isBlank()) {
            return new FieldMySqlValue(field, mySqlFieldInfo.type(), tableName);
        } else {
            return new FieldMySqlValue(field, mySqlFieldInfo.type(), fieldName, tableName);
        }
    }

    <T> List<FieldMySqlValue> getNonPrimaryKeyFieldMySqlValues(Class<T> tClass) {
        List<FieldMySqlValue> getters = new ArrayList<>();

        for (Field field : getDeclaredSqlFieldsOnly(tClass)) {
            if (!isPrimary(field)) {
                getters.add(fieldToFieldMySqlValue(tClass, field));
            }
        }

        return getters;
    }

    <T> List<FieldMySqlValue> getFieldMySqlValues(Class<T> tClass) {
        List<FieldMySqlValue> getters = new ArrayList<>();

        for (Field field : getDeclaredSqlFieldsOnly(tClass)) {
            if (!isPrimary(field) || isPrimaryKeyValueKnown(field)) {
                getters.add(fieldToFieldMySqlValue(tClass, field));
            }
        }

        return getters;
    }

    <T> List<Pair<FieldMySqlValue, FieldValueGetter>> getNestedFieldsMySqlValue(Class<T> tClass) {//todo wtf are these
        List<Pair<FieldMySqlValue, FieldValueGetter>> nestedFieldsMySqlValues = new ArrayList<>();
        List<Field> fields = getDeclaredNestedMySqlFields(tClass);
        fields.forEach(nestedField -> {
            nestedField.setAccessible(true);

            MySqlField mySqlField = nestedField.getAnnotation(MySqlField.class);
            if (mySqlField.primary()) {
                nestedFieldsMySqlValues.add(new Pair<>(new NestedPrimaryFieldMySqlValue(getPrimaryKeyFieldMySqlValue(nestedField.getType()), nestedField), fieldToFieldValueGetter(nestedField)));
            } else {
                nestedFieldsMySqlValues.add(new Pair<>(new NestedFieldMySqlValue(getPrimaryKeyFieldMySqlValue(nestedField.getType()), nestedField), fieldToFieldValueGetter(nestedField)));
            }
        });

        return nestedFieldsMySqlValues;
    }

    <T> List<JoinInfo> getJoinInfos(Class<T> modelClass) {
        return getJoinInfos(modelClass, new ArrayList<>());
    }

    <T> Class<?> getPrimaryFieldType(Class<T> modelClass) {
        return getDeclaredPrimaryField(modelClass).getType();
    }

    <T> ResultSetToInstance<T> getResultSetToInstance(Class<T> modelClass) {
        return getResultSetToInstance(modelClass, new ArrayList<>());
    }

    private <T> FieldValueGetter getPrimaryKeyFieldValueGetter(Class<T> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        return fieldToFieldValueGetter(field);
    }

    private <T> FieldValueGetter getNestedPrimaryKeyFieldValueGetter(Class<T> tClass) {
        Field field = getDeclaredPrimaryField(tClass);
        if (field.getAnnotation(Nested.class) != null) {
            field.setAccessible(true);
            return new NestedFieldValueGetter(field, getPrimaryKeyFieldValueGetter(field.getType()));
        }

        return null;
    }

    private <T> SpecificFieldValueSetter<T> getPrimaryKeyValueSetter(Class<T> tClass) {
        String tableName = getTableNameLowercase(tClass);
        Field field = getDeclaredPrimaryField(tClass);
        field.setAccessible(true);

        if (field.getAnnotation(Nested.class) != null) {
            return (SpecificFieldValueSetter<T>) new LazyNestedObjectValueSetter<>(field, tableName, getInstantiator(field.getType()), getPrimaryKeyValueSetter(field.getType()));
        }
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return new SpecificFieldValueSetter<>(field, tableName);
        } else {
            return new SpecificFieldValueSetter<>(field, fieldName, tableName);
        }
    }

    private <T> List<Pair<SpecificFieldValueOverride<T>, SpecificFieldValueOverride<T>>> getSpecificListFieldValueOverrides(Class<T> tClass) {
        List<Pair<SpecificFieldValueOverride<T>, SpecificFieldValueOverride<T>>> overrides = new ArrayList<>();

        List<Field> nestedLists = getDeclaredNestedFields(tClass).stream().filter(field -> isList(field.getGenericType())).toList();

        for (Field field : nestedLists) {
            field.setAccessible(true);
            overrides.add(new Pair<>(new SpecificListFieldValueOverride<>(field), new SpecificListFieldValueOverride<>(field)));
        }

        return overrides;
    }

    private FieldsMerger getFieldsMerger(Class<?> modelClass) {
        return getFieldsMerger(modelClass, new ArrayList<>());
    }

    private FieldsMerger getFieldsMerger(Class<?> modelClass, List<Class<?>> observedClasses) {
        observedClasses.add(modelClass);
        FieldsMerger fieldsMerger;
        FieldValueGetter nestedPrimaryKeyValueGetter = getNestedPrimaryKeyFieldValueGetter(modelClass);

        boolean hasDescendantWithList = getJoinInfos(modelClass).stream().anyMatch(JoinInfo::hasAnyList);
        if (!getSpecificListFieldValueOverrides(modelClass).isEmpty() || hasDescendantWithList) {
            fieldsMerger = new FieldsMerger(nestedPrimaryKeyValueGetter != null ? nestedPrimaryKeyValueGetter : getPrimaryKeyFieldValueGetter(modelClass), myNestedModelsDTOs(modelClass, observedClasses));
        } else {
            fieldsMerger = new DeadEnd(nestedPrimaryKeyValueGetter != null ? nestedPrimaryKeyValueGetter : getPrimaryKeyFieldValueGetter(modelClass), myNestedModelsDTOs(modelClass, observedClasses));
        }

        return fieldsMerger;
    }

    private List<FieldsMergerDTO> myNestedModelsDTOs(Class<?> tClass, List<Class<?>> observedClasses) {
        List<Field> flatNested = getDeclaredNestedFields(tClass);

        return flatNested.stream().map(f -> {
            f.setAccessible(true);
            Class<?> type = joiningWith(f, f.getGenericType(), isList(f.getGenericType()));

            Crossroads crossroads;

            FieldsMerger fieldsMerger;

            if (observedClasses.contains(type)) {
                fieldsMerger = new DeadEnd(getPrimaryKeyFieldValueGetter(type), null);
            } else {
                fieldsMerger = getFieldsMerger(type, observedClasses);
            }

            if (isList(f.getGenericType())) {
                crossroads = new ListRoad(fieldsMerger, new FieldValueGetter(f));
            } else if (getJoinInfos(type).stream().anyMatch(JoinInfo::hasAnyList)) { //todo isto kao else

                crossroads = new SingleRoad(fieldsMerger, new FieldValueGetter(f));

            } else {
                crossroads = new SingleRoad(fieldsMerger, new FieldValueGetter(f));
            }
            return new FieldsMergerDTO(new FieldValueSetter(f), crossroads);
        }).toList();
    }

    private <T> List<SpecificNestedFieldValueSetter<T>> getSpecificNestedFieldValueSetters(Class<T> tClass, List<Class<T>> observedClasses) {
        observedClasses.add(tClass);
        List<SpecificNestedFieldValueSetter<T>> setters = new ArrayList<>();
        for (Field field : getDeclaredNestedFields(tClass)) {
            Type genericType = field.getGenericType();
            Class<T> actualTypeArgument = (Class<T>) joiningWith(field, genericType, isList(genericType));
            field.setAccessible(true);
            if (isList(genericType)) {
                setters.add(new SpecificNestedListFieldValueSetter<>(field, getResultSetToInstance(actualTypeArgument, observedClasses), getInstantiator(actualTypeArgument), getPrimaryKeyValueSetter(actualTypeArgument)));
            } else {
                setters.add(new SpecificNestedFieldValueSetter<>(field, getResultSetToInstance(actualTypeArgument, observedClasses), getInstantiator(actualTypeArgument), getPrimaryKeyValueSetter(actualTypeArgument)));
            }
        }

        return setters;
    }

    String getPrimaryKeyFieldName(Class<?> ofClass) {
        Field field = getDeclaredPrimaryField(ofClass);
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        String fieldName = mySqlFieldInfo.column();

        if (fieldName.isBlank()) {
            return field.getName();
        }

        return fieldName;
    }

    private <T> List<JoinInfo> getJoinInfos(Class<T> modelClass, List<Class<?>> observedClasses) {
        List<JoinInfo> joinInfos = new ArrayList<>();
        observedClasses.add(modelClass);

        boolean hasAnyList = false;
        for (Field field : getDeclaredNestedFields(modelClass)) {
            Type genericType = field.getGenericType();
            if (isList(genericType)) {
                hasAnyList = true;
                break;
            }
        }

        for (Field field : getDeclaredNestedFields(modelClass)) {
            Nested nestingInfo = field.getAnnotation(Nested.class);
            Type genericType = field.getGenericType();
            boolean isList = isList(genericType);
            validateNestingDirection(nestingInfo, isList);
            Class<?> joiningWithClass = joiningWith(field, genericType, isList);

            String modelClassNameLowerCase = getTableNameLowercase(modelClass);
            String targetTableName = getTableName(joiningWithClass);
            String targetTableLowercase = getTableNameLowercase(joiningWithClass);

            String nestedPrimaryKeyFieldName;

            if (getPrimaryFieldType(joiningWithClass).equals(modelClass)) {
                nestedPrimaryKeyFieldName = getPrimaryKeyFieldName(modelClass);
            } else {
                nestedPrimaryKeyFieldName = getPrimaryKeyFieldName(joiningWithClass);
            }

            List<ColumnNameAndAlias> columnNameAndAliases = getColumnNameAndAliases(nestingInfo, joiningWithClass, targetTableLowercase);

            if (!isList) {
                String link = nestingInfo.link();
                if (nestingInfo.outward()) {
                    if (isPrimary(field)) {
                        JoinInfo e = new JoinInfo(targetTableName, targetTableLowercase, nestedPrimaryKeyFieldName, modelClassNameLowerCase, nestedPrimaryKeyFieldName, columnNameAndAliases, hasAnyList);
                        joinInfos.add(e);
                    } else {
                        JoinInfo e = new JoinInfo(targetTableName, targetTableLowercase, nestedPrimaryKeyFieldName, modelClassNameLowerCase, getOutwardJoinForeignKey(link, field, joiningWithClass), columnNameAndAliases, hasAnyList);
                        joinInfos.add(e);
                    }
                }

                if (nestingInfo.inward()) {
                    joinInfos.add(new JoinInfo(targetTableName, targetTableLowercase, nestedPrimaryKeyFieldName, modelClassNameLowerCase, getPrimaryKeyFieldMySqlValue(modelClass).getFieldName(), columnNameAndAliases, hasAnyList));
                }
            } else { //todo currently works only for N:N, cannot read 1:N
                String connectingTable = nestingInfo.connection().isEmpty() ? connectingTable(getTableName(modelClass), targetTableName) : nestingInfo.connection();
                String connectingTableLowercase = connectingTable.toLowerCase();
                String foreignKeyFrom = nestingInfo.from().isEmpty() ? connectingTable(getTableNameLowercase(joiningWithClass), nestedPrimaryKeyFieldName) : nestingInfo.from();
                String foreignKeyTo = nestingInfo.to().isEmpty() ? connectingTable(modelClassNameLowerCase, getPrimaryKeyFieldMySqlValue(modelClass).getFieldName()) : nestingInfo.to();

                JoinInfo joinToConnectingTable = new JoinInfo(connectingTable, connectingTableLowercase, foreignKeyTo, modelClassNameLowerCase, nestedPrimaryKeyFieldName, columnNameAndAliases, true, hasAnyList, modelClass, joiningWithClass);
                joinInfos.add(joinToConnectingTable);
                JoinInfo joinFromConnectingTableToTarget = new JoinInfo(targetTableName, targetTableLowercase, nestedPrimaryKeyFieldName, connectingTableLowercase, foreignKeyFrom, columnNameAndAliases, true, hasAnyList, modelClass, joiningWithClass);
                joinInfos.add(joinFromConnectingTableToTarget);
            }

            List<JoinInfo> nestedJoins = null;

            if (!observedClasses.contains(joiningWithClass)) {
                nestedJoins = getJoinInfos(joiningWithClass, observedClasses);
            }
            if (nestedJoins != null) joinInfos.addAll(nestedJoins);

        }


        return joinInfos;
    }

    <T> Field getDeclaredPrimaryField(Class<T> tClass) {
        List<Field> primaryKeys = Arrays.stream(tClass.getDeclaredFields()).filter(field -> {
            MySqlField annotation = field.getAnnotation(MySqlField.class);
            return annotation != null && annotation.primary();
        }).toList();

        if (primaryKeys.isEmpty()) throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
        else if (primaryKeys.size() > 1) throw new RuntimeException(THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY);
        else if (primaryKeys.get(0).getGenericType().getTypeName().contains("List"))
            throw new RuntimeException(PRIMARY_KEY_CANNOT_BE_A_LIST);

        return primaryKeys.get(0);
    }

    private FieldValueGetter fieldToFieldValueGetter(Field field) {
        field.setAccessible(true);
        return new FieldValueGetter(field);
    }

    private <T> FieldMySqlValue fieldToFieldMySqlValue(Class<T> tClass, Field field) {
        field.setAccessible(true);
        MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);
        String tableName = getTableNameLowercase(tClass);
        String fieldName = mySqlFieldInfo.column();
        if (fieldName.isBlank()) {
            return (new FieldMySqlValue(field, mySqlFieldInfo.type(), tableName));
        } else {
            return (new FieldMySqlValue(field, mySqlFieldInfo.type(), fieldName, tableName));
        }
    }

    private String connectingTable(String modelClass, String targetTableLowercase) {
        return modelClass + "_" + targetTableLowercase;
    }

    private String getOutwardJoinForeignKey(String link, Field field, Class<?> modelClass) {
        if (link.isBlank()) {
            return connectingTable(getTableNameLowercase(field.getType()), getPrimaryKeyFieldMySqlValue(modelClass).getFieldName());
        } else {
            return link;
        }
    }

    private boolean isPrimary(Field field) {
        MySqlField annotation = field.getAnnotation(MySqlField.class);

        return annotation != null && annotation.primary();
    }

    private boolean isPrimaryKeyValueKnown(Field field) {
        return isPrimary(field) && field.getAnnotation(MySqlField.class).known();
    }

    //todo expand to support infinite nesting, right now it's only 1 level deep
    private List<ColumnNameAndAlias> getColumnNameAndAliases(Nested nestingInfo, Class<?> targetClass, String targetTableLowercase) {
        List<ColumnNameAndAlias> columnNameAndAliases;
        if (nestingInfo.eager()) {
            columnNameAndAliases = getSpecificFieldValueSetters(targetClass).stream().map(specificFieldValueSetter -> specificFieldValueSetter.fieldNameAndAlias(targetTableLowercase)).toList();
        } else {
            columnNameAndAliases = List.of(getPrimaryKeyValueSetter(targetClass).fieldNameAndAlias(targetTableLowercase));
        }
        return columnNameAndAliases;
    }

    private void validateNestingDirection(Nested nestingInfo, boolean isList) {
        if (!isList && nestingInfo.outward() == nestingInfo.inward()) {
            throw new RuntimeException(NESTING_DIRECTION_NEEDS_TO_BE_IN_ONE_DIRECTION);
        }
    }

    private boolean isList(Type genericType) {
        return genericType.getTypeName().contains("List");
    }

    private Class<?> joiningWith(Field field, Type genericType, boolean isList) {
        if (isList) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            return field.getType();
        }
    }

    private <T> List<Field> getDeclaredSqlFieldsOnly(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(MySqlField.class) != null && field.getAnnotation(Nested.class) == null).collect(Collectors.toList());
    }

    private <T> List<Field> getDeclaredNestedFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(Nested.class) != null).collect(Collectors.toList());
    }

    private <T> List<Field> getDeclaredNestedMySqlFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(Nested.class) != null && field.getAnnotation(MySqlField.class) != null).collect(Collectors.toList());
    }

    private <T> ResultSetToInstance<T> getResultSetToInstance(Class<T> modelClass, List<Class<T>> observedClasses) {
        ResultSetToInstance<T> resultSetToInstance;
        boolean hasNestedList = getJoinInfos(modelClass).stream().anyMatch(JoinInfo::isListJoin);

        List<SpecificNestedFieldValueSetter<T>> specificNestedFieldValueSetters;

        if (observedClasses.contains(modelClass)) {
            specificNestedFieldValueSetters = List.of();
        } else {
            specificNestedFieldValueSetters = getSpecificNestedFieldValueSetters(modelClass, observedClasses);
        }
        if (hasNestedList) {
            resultSetToInstance = new ResultSetToInstanceWithListNesting<>(getInstantiator(modelClass), getSpecificFieldValueSetters(modelClass), specificNestedFieldValueSetters, modelClass, getFieldsMerger(modelClass));
        } else if (!specificNestedFieldValueSetters.isEmpty()) {
            resultSetToInstance = new ResultSetToInstanceWithNesting<>(getInstantiator(modelClass), getSpecificFieldValueSetters(modelClass), specificNestedFieldValueSetters, modelClass);
        } else {
            resultSetToInstance = new ResultSetToInstance<>(getInstantiator(modelClass), getSpecificFieldValueSetters(modelClass));
        }

        return resultSetToInstance;
    }

    <T> LinkedHashSet<ColumnNameAndAlias> getColumnsAndAliases(Class<T> modelClass) {
        LinkedHashSet<ColumnNameAndAlias> columnNameAndAliases = new LinkedHashSet<>();

        for (SpecificFieldValueSetter<T> specificFieldValueSetter : getSpecificFieldValueSetters(modelClass)) {
            columnNameAndAliases.add(specificFieldValueSetter.fieldNameAndAlias(getTableNameLowercase(modelClass)));
        }

        for (JoinInfo joinInfo : getJoinInfos(modelClass)) {
            columnNameAndAliases.addAll(joinInfo.fieldNameAndAliases());
        }
        return columnNameAndAliases;
    }

    <T> FilteredSelect getFilteredSelect(Class<T> modelClass) {
        if (getJoinInfos(modelClass).isEmpty()) {
            return new FilteredSelect(getColumnsAndAliases(modelClass), getTableName(modelClass));
        } else {
            return new JoinedFilterSelect(getColumnsAndAliases(modelClass), getTableName(modelClass), getJoinInfos(modelClass));
        }
    }

    <T> MySqlValuesFilter<T> getMySqlValuesFilter(Class<T> modelClass) {
        if (getNestedFieldsMySqlValue(modelClass).isEmpty()) {
            return new NonPrimaryMySqlValuesFilter<>(getFieldMySqlValues(modelClass));
        } else {
            if (getDeclaredPrimaryField(modelClass).getAnnotation(Nested.class) != null)
                return new MySqlValuesFilterWithNestedPrimaryKey<>(getNonPrimaryKeyFieldMySqlValues(modelClass), getNestedFieldsMySqlValue(modelClass));

            return new NonPrimaryNestedMySqlValuesFilter<>(getFieldMySqlValues(modelClass), getNestedFieldsMySqlValue(modelClass));
        }
    }

    <T> Map<String, FieldMySqlValue> getAliasFieldMySqlValues(Class<T> modelClass) {
        HashMap<String, FieldMySqlValue> toReturn = new HashMap<>();
        FieldMySqlValue primaryKeyFieldMySqlValue = getPrimaryKeyFieldMySqlValue(modelClass);

        toReturn.put(primaryKeyFieldMySqlValue.alias(), primaryKeyFieldMySqlValue);

        final List<FieldMySqlValue> nonPrimaryKeyFieldMySqlValues = getNonPrimaryKeyFieldMySqlValues(modelClass);
        nonPrimaryKeyFieldMySqlValues.forEach(fieldMySqlValue -> toReturn.put(fieldMySqlValue.alias(), fieldMySqlValue));

        return toReturn;
    }
}
