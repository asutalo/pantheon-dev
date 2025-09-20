package com.eu.atit.mysql.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

class SelectQueryResultProcessor implements Function<PreparedStatement, List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> apply(PreparedStatement preparedStatement) {
        try {
            List<Map<String, Object>> fetchedRows = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Map<String, Integer> columnLabelsAndIndexes = columnLabelsAndIndexes(resultSet);

                processRow(columnLabelsAndIndexes, fetchedRows, resultSet);

                while (resultSet.next()) {
                    processRow(columnLabelsAndIndexes, fetchedRows, resultSet);
                }
            }

            //todo configurable logging of rows/query
            System.out.println("-------------------------");
            System.out.println("Fetched rows:");
            System.out.println(fetchedRows);
            System.out.println("-------------------------");
            return fetchedRows;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    private Map<String, Integer> columnLabelsAndIndexes(ResultSet resultSet) throws SQLException {
        Map<String, Integer> columnLabelsAndIndexes = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 0; i < columnCount; i++) {
            int columnIndex = i + 1;
            columnLabelsAndIndexes.put(metaData.getColumnLabel(columnIndex), columnIndex);
        }

        return columnLabelsAndIndexes;
    }

    private void processRow(Map<String, Integer> columnsAndIndexes, List<Map<String, Object>> result, ResultSet resultSet) throws SQLException {
        Map<String, Object> row = new HashMap<>();

        for (Entry<String, Integer> entry : columnsAndIndexes.entrySet()) {
            row.put(entry.getKey(), resultSet.getObject(entry.getValue()));
        }
        result.add(row);
    }
}

// todo refactor to stream data from DB instead of collecting it in memory
//class SelectQueryResultProcessor implements Function<PreparedStatement, Stream<Map<String, Object>>> {
//    @Override
//    public Stream<Map<String, Object>> apply(PreparedStatement preparedStatement) {
//        try {
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (!resultSet.next()) {
//                return Stream.empty();
//            }
//            Map<String, Integer> columnLabelsAndIndexes = columnLabelsAndIndexes(resultSet);
//
//            Iterator<Map<String, Object>> rowIterator = new Iterator<>() {
//                boolean hasCurrent = true;
//
//                @Override
//                public boolean hasNext() {
//                    return hasCurrent;
//                }
//
//                @Override
//                public Map<String, Object> next() {
//                    try {
//                        Map<String, Object> row = new HashMap<>();
//                        for (Entry<String, Integer> entry : columnLabelsAndIndexes.entrySet()) {
//                            row.put(entry.getKey(), resultSet.getObject(entry.getValue()));
//                        }
//                        // Advance cursor
//                        hasCurrent = resultSet.next();
//                        return row;
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            };
//
//            // Important: Close resources when stream is closed!
//            todo important  prepared statements are closed automatically in the mysql client, this needs changing as we MUST let the stream close the resources
//            return StreamSupport.stream(
//                            Spliterators.spliteratorUnknownSize(rowIterator, Spliterator.ORDERED), false)
//                    .onClose(() -> {
//                        try { resultSet.close(); preparedStatement.close(); } catch (SQLException ignored) {}
//                    });
//
//        } catch (SQLException sqlException) {
//            throw new RuntimeException(sqlException);
//        }
//    }
//
//    // ...columnLabelsAndIndexes (as before)...
//}