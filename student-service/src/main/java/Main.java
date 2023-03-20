import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.eu.atit.student.service.model.Course;
import com.eu.atit.student.service.model.Diploma;
import com.eu.atit.student.service.model.Student;
import com.eu.atit.student.service.model.Type;
import com.google.inject.TypeLiteral;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("student_service", "student_service_user", "devuser", "student_service_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));
        MySQLServiceProvider mySQLServiceProvider = new MySQLServiceProvider(dataClient);

        MySQLService<Student> studentMySQLService = (MySQLService<Student>) mySQLServiceProvider.provide(TypeLiteral.get(Student.class));
        MySQLService<Course> courseMySQLService = (MySQLService<Course>) mySQLServiceProvider.provide(TypeLiteral.get(Course.class));
        MySQLService<Type> typeMySQLService = (MySQLService<Type>) mySQLServiceProvider.provide(TypeLiteral.get(Type.class));
        MySQLService<Diploma> diplomaMySQLService = (MySQLService<Diploma>) mySQLServiceProvider.provide(TypeLiteral.get(Diploma.class));


//        Student i = new Student();
//        i.setId(4);
//        Student i = new Student("shime", new Type(1), null, List.of());
//        studentMySQLService.save(i);
//        studentMySQLService.getAll().forEach(System.out::println);
//        diplomaMySQLService.save(new Diploma(i, true));
//        diplomaMySQLService.getAll().forEach(System.out::println);
//        typeMySQLService.getAll().forEach(System.out::println);
//        courseMySQLService.getAll().forEach(System.out::println);
//
//
        typeExamples(mySQLServiceProvider);
    }

    private static void typeExamples(MySQLServiceProvider mySQLServiceProvider) throws SQLException {
        MySQLService<Type> typeMySQLService = (MySQLService<Type>) mySQLServiceProvider.provide(TypeLiteral.get(Type.class));

        //fetch all
        System.out.println(typeMySQLService.getAll());

        //create and insert new
        Type newType = new Type("testType");
        typeMySQLService.save(newType);
        System.out.println(typeMySQLService.getAll());

        //update new row
        newType.setName("new type name");
        typeMySQLService.update(newType);

        //delete by reference
        typeMySQLService.delete(newType);
        System.out.println(typeMySQLService.getAll());

        //create, insert new, and delete by filtering
        Type otherNewType = new Type("testTypeDeleteById");
        typeMySQLService.save(otherNewType);

        System.out.println(typeMySQLService.getAll());

        typeMySQLService.delete(typeMySQLService.get(Map.of("type.id", otherNewType.id())));
        System.out.println(typeMySQLService.getAll());
    }
}