import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.mysql.cj.MysqlType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MainQueryBuilderTransactionary {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("student_service", "student_service_user", "devuser", "student_service_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));

        Connection connection = dataClient.startTransaction();

        System.out.println("selected all: " + dataClient.executeSelectQuery(selectAll()));

        int insertedId = dataClient.executeInsertQuery(insertNewElement(), connection);
        System.out.println("inserted new element with id:" + insertedId);

        //despite the new insert ID being created, as we haven't ended the transaction the new element is not present in DB
        System.out.println("selected all after insert during transaction: " + dataClient.executeSelectQuery(selectAll()));

        dataClient.endTransaction(connection);

        //the transaction has been ended so new element is now present in DB
        System.out.println("selected all after ending transaction: " + dataClient.executeSelectQuery(selectAll()));

        //we're deleting the new element in regular fashion as we no longer
        dataClient.executeOtherDmlQuery(deleteById(insertedId));
        System.out.println("selected all after delete: " + dataClient.executeSelectQuery(selectAll()));

        //creating a brand new Connection for a new transaction
        connection = dataClient.startTransaction();

        //adding two new elements
        insertedId = dataClient.executeInsertQuery(insertNewElement(), connection);
        System.out.println("inserted new element with id:" + insertedId);

        insertedId = dataClient.executeInsertQuery(insertNewElement(), connection);
        System.out.println("inserted new element with id:" + insertedId);

        //deleting one of the new elements inside transaction
        dataClient.executeOtherDmlQuery(deleteById(insertedId), connection);

        //finally rolling back the addition and removal of new elements
        dataClient.rollbackTransaction(connection);
        dataClient.endTransaction(connection);

        //due to rollback we should not see the one new element that was inserted and not deleted
        System.out.println("selected all after after rollback: " + dataClient.executeSelectQuery(selectAll()));
    }

    private static QueryBuilder deleteById(int insertedId) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from("type");
        queryBuilder.where();
        queryBuilder.keyIsVal(new MySqlValue(MysqlType.INT, "id", insertedId));
        return queryBuilder;
    }

    private static QueryBuilder insertNewElement() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert("type", new LinkedList<>(List.of(new MySqlValue(MysqlType.VARCHAR, "name", UUID.randomUUID().toString()))));
        return queryBuilder;
    }

    private static QueryBuilder selectAll() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from("type");
        return queryBuilder;
    }
}