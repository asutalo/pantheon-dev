package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.LeftJoin;
import com.eu.atit.mysql.query.QueryBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class JoinedFilterSelect extends FilteredSelect {
    private final List<LeftJoin> leftJoins = new ArrayList<>();

    JoinedFilterSelect(LinkedHashSet<ColumnNameAndAlias> columnsAndAliases, String tableName, List<JoinInfo> joinInfos) {
        super(columnsAndAliases, tableName);
        List<String> combinations = new ArrayList<>();
        for (JoinInfo joinInfo : joinInfos) {

            String sourceJoinInfo = joinInfo.sourceTableAlias().concat(".").concat(joinInfo.sourceId()) + " = " + joinInfo.targetTableAlias().concat(".".concat(joinInfo.targetId()));
            String targetJoinInfo = joinInfo.targetTableAlias().concat(".".concat(joinInfo.targetId())) + " = " + joinInfo.sourceTableAlias().concat(".").concat(joinInfo.sourceId());

            if (!combinations.contains(targetJoinInfo) && !combinations.contains(sourceJoinInfo)) {
                combinations.add(targetJoinInfo);
                combinations.add(sourceJoinInfo);
                leftJoins.add(new LeftJoin(joinInfo.targetTableName(), joinInfo.targetId(), joinInfo.sourceTableAlias(), joinInfo.sourceId()));
            }
        }
    }

    @Override
    QueryBuilder get() {
        QueryBuilder queryBuilder = super.get();
        leftJoins.forEach(queryBuilder::leftJoin);
        return queryBuilder;
    }
}
