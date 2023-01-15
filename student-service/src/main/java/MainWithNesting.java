import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.mysql.service.ColumnNameAndAlias;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainWithNesting {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("student_service", "student_service_user", "devuser", "student_service_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));

        System.out.println("selected all: " + dataClient.executeSelectQuery(selectAll()));
    }

    private static QueryBuilder selectAll() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(new ArrayList<>(List.of(new ColumnNameAndAlias("student.name", "student_name"), new ColumnNameAndAlias("type.name", "type_name"))));
        queryBuilder.from("student");
        queryBuilder.leftJoin("type", "id", "student", "id");
        return queryBuilder;
    }
}
