package com.eu.atit.mysql.service;

import com.eu.atit.mysql.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class JoinedFilterSelect extends FilteredSelect{
    private final QueryBuilder joinedFilteredSelectQueryBuilder;

    JoinedFilterSelect(MySQLModelDescriptor<?> mySQLModelDescriptor) {
        super(mySQLModelDescriptor);

        QueryBuilder queryBuilder = super.get();

        List<String> combinations = new ArrayList<>();
        for (JoinInfo joinInfo : mySQLModelDescriptor.getJoinInfos()) {

            String sourceJoinInfo = joinInfo.sourceTableName().concat(".").concat(joinInfo.sourceId()) + " = " + joinInfo.targetTableLowercase().concat(".".concat(joinInfo.targetId()));
            String targetJoinInfo = joinInfo.targetTableLowercase().concat(".".concat(joinInfo.targetId())) + " = " + joinInfo.sourceTableName().concat(".").concat(joinInfo.sourceId());

            if (!combinations.contains(targetJoinInfo) && !combinations.contains(sourceJoinInfo)) {
                combinations.add(targetJoinInfo);
                combinations.add(sourceJoinInfo);
                queryBuilder.leftJoin(joinInfo.targetTableName(), joinInfo.targetId(), joinInfo.sourceTableName(), joinInfo.sourceId());
            }
        }

        joinedFilteredSelectQueryBuilder = queryBuilder;
    }

    @Override
    QueryBuilder get(){
        return joinedFilteredSelectQueryBuilder;
    }
}
