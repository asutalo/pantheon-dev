package com.eu.atit.mysql.service;

import java.util.List;
import java.util.Objects;

public class JoinInfo {
    private final String targetTableName;
    private final String targetTableAlias;
    private final String targetId;
    private final String sourceTableAlias;
    private final String sourceId;
    private final List<ColumnNameAndAlias> columnNameAndAliases;
    private final boolean isListJoin;
    private final boolean hasAnyList;
    private final Class<?> parent;
    private final Class<?> child;

    public JoinInfo(String targetTableName, String targetTableAlias, String targetId, String sourceTableAlias, String sourceId, List<ColumnNameAndAlias> columnNameAndAliases, boolean isListJoin, boolean hasAnyList, Class<?> parent, Class<?> child) {
        this.targetTableName = targetTableName;
        this.targetTableAlias = targetTableAlias;
        this.targetId = targetId;
        this.sourceTableAlias = sourceTableAlias;
        this.sourceId = sourceId;
        this.columnNameAndAliases = columnNameAndAliases;
        this.isListJoin = isListJoin;
        this.hasAnyList = hasAnyList;
        this.parent = parent;
        this.child = child;
    }

    public JoinInfo(String targetTableName, String targetTableAlias, String targetId, String sourceTableAlias, String sourceId, List<ColumnNameAndAlias> columnNameAndAliases, boolean hasAnyList) {
        this(targetTableName, targetTableAlias, targetId, sourceTableAlias, sourceId, columnNameAndAliases, false, hasAnyList, null, null);
    }


    public String targetTableName() {
        return targetTableName;
    }

    public String targetTableAlias() {
        return targetTableAlias;
    }

    public String targetId() {
        return targetId;
    }

    public String sourceTableAlias() {
        return sourceTableAlias;
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

    public boolean hasAnyList() {
        return hasAnyList;
    }

    public Class<?> getParent() {
        return parent;
    }

    public Class<?> getChild() {
        return child;
    }

    @Override
    public String toString() {
        return "JoinInfo{" +
               "targetTableName='" + targetTableName + '\'' +
               ", targetTableAlias='" + targetTableAlias + '\'' +
               ", targetId='" + targetId + '\'' +
               ", sourceTableAlias='" + sourceTableAlias + '\'' +
               ", sourceId='" + sourceId + '\'' +
               ", columnNameAndAliases=" + columnNameAndAliases +
               ", isListJoin=" + isListJoin +
               ", hasAnyList=" + hasAnyList +
               ", parent=" + parent +
               ", child=" + child +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinInfo joinInfo = (JoinInfo) o;

        if (isListJoin != joinInfo.isListJoin) return false;
        if (hasAnyList != joinInfo.hasAnyList) return false;
        if (!Objects.equals(targetTableName, joinInfo.targetTableName))
            return false;
        if (!Objects.equals(targetTableAlias, joinInfo.targetTableAlias))
            return false;
        if (!Objects.equals(targetId, joinInfo.targetId)) return false;
        if (!Objects.equals(sourceTableAlias, joinInfo.sourceTableAlias))
            return false;
        if (!Objects.equals(sourceId, joinInfo.sourceId)) return false;
        if (!Objects.equals(columnNameAndAliases, joinInfo.columnNameAndAliases))
            return false;
        if (!Objects.equals(parent, joinInfo.parent)) return false;
        return Objects.equals(child, joinInfo.child);
    }

    @Override
    public int hashCode() {
        int result = targetTableName != null ? targetTableName.hashCode() : 0;
        result = 31 * result + (targetTableAlias != null ? targetTableAlias.hashCode() : 0);
        result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
        result = 31 * result + (sourceTableAlias != null ? sourceTableAlias.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (columnNameAndAliases != null ? columnNameAndAliases.hashCode() : 0);
        result = 31 * result + (isListJoin ? 1 : 0);
        result = 31 * result + (hasAnyList ? 1 : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (child != null ? child.hashCode() : 0);
        return result;
    }
}
