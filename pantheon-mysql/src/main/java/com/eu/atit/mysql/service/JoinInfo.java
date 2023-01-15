package com.eu.atit.mysql.service;

import java.util.List;
import java.util.Objects;

public class JoinInfo {
    private final String targetTableName;
    private final String targetTableLowercase;
    private final String targetId;
    private final String sourceTableName;
    private final String sourceId;
    private final List<ColumnNameAndAlias> columnNameAndAliases;
    private final boolean isListJoin;

    public JoinInfo(String targetTableName, String targetTableLowercase, String targetId, String sourceTableName, String sourceId, List<ColumnNameAndAlias> columnNameAndAliases, boolean isListJoin) {
        this.targetTableName = targetTableName;
        this.targetTableLowercase = targetTableLowercase;
        this.targetId = targetId;
        this.sourceTableName = sourceTableName;
        this.sourceId = sourceId;
        this.columnNameAndAliases = columnNameAndAliases;
        this.isListJoin = isListJoin;
    }

    public JoinInfo(String targetTableName, String targetTableLowercase, String targetId, String sourceTableName, String sourceId, List<ColumnNameAndAlias> columnNameAndAliases) {
        this(targetTableName, targetTableLowercase, targetId, sourceTableName, sourceId, columnNameAndAliases, false);
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

    public boolean isListJoin() {
        return isListJoin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinInfo joinInfo = (JoinInfo) o;

        if (isListJoin != joinInfo.isListJoin) return false;
        if (!Objects.equals(targetTableName, joinInfo.targetTableName))
            return false;
        if (!Objects.equals(targetTableLowercase, joinInfo.targetTableLowercase))
            return false;
        if (!Objects.equals(targetId, joinInfo.targetId)) return false;
        if (!Objects.equals(sourceTableName, joinInfo.sourceTableName))
            return false;
        if (!Objects.equals(sourceId, joinInfo.sourceId)) return false;
        return Objects.equals(columnNameAndAliases, joinInfo.columnNameAndAliases);
    }

    @Override
    public int hashCode() {
        int result = targetTableName != null ? targetTableName.hashCode() : 0;
        result = 31 * result + (targetTableLowercase != null ? targetTableLowercase.hashCode() : 0);
        result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
        result = 31 * result + (sourceTableName != null ? sourceTableName.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (columnNameAndAliases != null ? columnNameAndAliases.hashCode() : 0);
        result = 31 * result + (isListJoin ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JoinInfo{" +
                "targetTableName='" + targetTableName + '\'' +
                ", targetTableLowercase='" + targetTableLowercase + '\'' +
                ", targetId='" + targetId + '\'' +
                ", sourceTableName='" + sourceTableName + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", columnNameAndAliases=" + columnNameAndAliases +
                ", isListJoin=" + isListJoin +
                '}';
    }
}
