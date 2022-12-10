package com.eu.atit.mysql.service;

import java.util.List;
import java.util.Objects;

public  class JoinInfo {
    private final String targetTableName;
    private final String targetTableLowercase;
    private final String targetId;
    private final String sourceTableName;
    private final String sourceId;
    private final List<ColumnNameAndAlias> columnNameAndAliases;

    public JoinInfo(String targetTableName, String targetTableLowercase, String targetId, String sourceTableName, String sourceId, List<ColumnNameAndAlias> columnNameAndAliases) {
        this.targetTableName = targetTableName;
        this.targetTableLowercase = targetTableLowercase;
        this.targetId = targetId;
        this.sourceTableName = sourceTableName;
        this.sourceId = sourceId;
        this.columnNameAndAliases = columnNameAndAliases;
    }

    public JoinInfo(String targetTableName, String targetId, String sourceTableName, String sourceId, List<ColumnNameAndAlias> columnNameAndAliases) {
        this(targetTableName, targetTableName.toLowerCase(), targetId, sourceTableName, sourceId, columnNameAndAliases);
    }

    public String targetTableName() {
        return targetTableName;
    }

    public String targetTableLowercase() {
        return targetTableLowercase;
    }

    public String targetId() {
        return targetId;
    }

    public String sourceTableName() {
        return sourceTableName;
    }

    public String sourceId() {
        return sourceId;
    }

    public List<ColumnNameAndAlias> fieldNameAndAliases() {
        return columnNameAndAliases;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JoinInfo) obj;
        return Objects.equals(this.targetTableName, that.targetTableName) &&
                Objects.equals(this.targetTableLowercase, that.targetTableLowercase) &&
                Objects.equals(this.targetId, that.targetId) &&
                Objects.equals(this.sourceTableName, that.sourceTableName) &&
                Objects.equals(this.sourceId, that.sourceId) &&
                Objects.equals(this.columnNameAndAliases, that.columnNameAndAliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetTableName, targetTableLowercase, targetId, sourceTableName, sourceId, columnNameAndAliases);
    }

    @Override
    public String toString() {
        return "JoinInfo[" +
                "targetTableName=" + targetTableName + ", " +
                "targetTableLowercase=" + targetTableLowercase + ", " +
                "targetId=" + targetId + ", " +
                "sourceTableName=" + sourceTableName + ", " +
                "sourceId=" + sourceId + ", " +
                "fieldNameAndAliases=" + columnNameAndAliases + ']';
    }

}
