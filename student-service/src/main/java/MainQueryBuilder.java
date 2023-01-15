import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.mysql.cj.MysqlType;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MainQueryBuilder {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("student_service", "student_service_user", "devuser", "student_service_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));


        //select *
        //from student
        //join student_course sc on student.id = sc.student_id
        //join course c on c.id = sc.course_id;

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from("student");
        queryBuilder.leftJoin("student_course", "student_id", "student", "id");
        queryBuilder.leftJoin("course", "id", "student_course", "course_id");

        System.out.println(dataClient.executeSelectQuery(queryBuilder));


//        System.out.println("selected all: " + dataClient.executeSelectQuery(selectAll()));
//
//        int insertedId = dataClient.executeInsertQuery(insertNewElement());
//        System.out.println("inserted new element with id:" + insertedId);
//        System.out.println("selected all after insert: " + dataClient.executeSelectQuery(selectAll()));
//
//        dataClient.executeOtherDmlQuery(deleteById(insertedId));
//        System.out.println("selected all after delete: " + dataClient.executeSelectQuery(selectAll()));
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
        queryBuilder.insert("type", new LinkedList<>(List.of(new MySqlValue(MysqlType.VARCHAR, "name", "newType"))));
        return queryBuilder;
    }

    private static QueryBuilder selectAll() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from("type");
        return queryBuilder;
    }
}