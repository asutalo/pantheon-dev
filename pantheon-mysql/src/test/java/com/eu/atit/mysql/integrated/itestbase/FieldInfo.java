package com.eu.atit.mysql.integrated.itestbase;

public record FieldInfo(String tableName, String fieldName, boolean joinedOn, String joinedFromTable, String joinedFromField, String joinedOnField, boolean selectable) {
    public FieldInfo(String tableName, String fieldName) {
        this(tableName, fieldName, false, null, null, null, true);
    }
}
