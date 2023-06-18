package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.QueryBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JoinedFilterSelect extends FilteredSelect {
    private final QueryBuilder joinedFilteredSelectQueryBuilder;

    JoinedFilterSelect(LinkedHashSet<ColumnNameAndAlias> columnsAndAliases, String tableName, List<JoinInfo> joinInfos) {
        super(columnsAndAliases, tableName);

        QueryBuilder queryBuilder = super.get();

        List<String> combinations = new ArrayList<>();
        for (JoinInfo joinInfo : joinInfos) {

            String sourceJoinInfo = joinInfo.sourceTableAlias().concat(".").concat(joinInfo.sourceId()) + " = " + joinInfo.targetTableAlias().concat(".".concat(joinInfo.targetId()));
            String targetJoinInfo = joinInfo.targetTableAlias().concat(".".concat(joinInfo.targetId())) + " = " + joinInfo.sourceTableAlias().concat(".").concat(joinInfo.sourceId());

            if (!combinations.contains(targetJoinInfo) && !combinations.contains(sourceJoinInfo)) {
                combinations.add(targetJoinInfo);
                combinations.add(sourceJoinInfo);
                queryBuilder.leftJoin(joinInfo.targetTableName(), joinInfo.targetId(), joinInfo.sourceTableAlias(), joinInfo.sourceId());
            }
        }

        joinedFilteredSelectQueryBuilder = queryBuilder;
    }

    @Override
    QueryBuilder get() {
        return joinedFilteredSelectQueryBuilder;
    }
}
