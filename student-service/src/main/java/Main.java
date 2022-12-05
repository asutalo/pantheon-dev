import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.eu.atit.pantheon.service.Service;
import com.eu.atit.pantheon.service.ServiceProviderRegistry;
import com.eu.atit.student.service.model.Type;
import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        System.out.println("Hello world!");

        LinkedList<String> dbParams = new LinkedList<>(List.of("student_service", "student_service_user", "devuser", "student_service_user@localhost"));

        MySQLServiceProvider mySQLServiceProvider = new MySQLServiceProvider(new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams)));

        MySQLService<Type> typeMySQLService = (MySQLService<Type>) mySQLServiceProvider.provide(TypeLiteral.get(Type.class));

        System.out.println(typeMySQLService.getAll());

        Type newType = new Type("testType");

        Type savedType = typeMySQLService.save(newType);

        System.out.println(typeMySQLService.getAll());

        typeMySQLService.delete(savedType);

        System.out.println(typeMySQLService.getAll());

        Type otherNewType = new Type("testTypeDeleteById");

        Type otherSavedType = typeMySQLService.save(otherNewType);

        System.out.println(typeMySQLService.getAll());

        typeMySQLService.delete(typeMySQLService.get(Map.of("id", otherSavedType.id())));

        System.out.println(typeMySQLService.getAll());


    }
}