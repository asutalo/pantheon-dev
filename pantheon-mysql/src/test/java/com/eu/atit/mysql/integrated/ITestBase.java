package com.eu.atit.mysql.integrated;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.integrated.model.base.BaseType;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public abstract class ITestBase<S extends BaseStudent, T extends BaseType, D extends BaseDiploma> {
    T TEST_TYPE;

    Class<S> sClass;
    Class<T> tClass;
    Class<D> dClass;
    MySQLService<T> typeMySQLService;
    MySQLService<S> studentMySQLService;

    private static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final LinkedList<String> dbParams = new LinkedList<>(List.of("student_service_test", "student_service_test", "devtestuser", "student_service_test@localhost"));

    static MySQLServiceProvider mySQLServiceProvider;
    void setUp(Class<S> studentClass, Class<T> typeClass, Class<D> diplomaClass) throws SQLException, URISyntaxException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));
        prepareTestDB(dataClient);
        mySQLServiceProvider = new MySQLServiceProvider(dataClient);
        sClass = studentClass;
        tClass = typeClass;
        dClass = diplomaClass;
        initTEST_TYPE(typeClass);

        typeMySQLService = (MySQLService<T>) mySQLServiceProvider.provide(TypeLiteral.get(typeClass));
        studentMySQLService = (MySQLService<S>) mySQLServiceProvider.provide(TypeLiteral.get(studentClass));
        typeMySQLService.save(TEST_TYPE);
    }

    void shouldInsertNewStudent_withoutDiploma() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        int startingStudentCount = studentMySQLService.getAll().size();

        S testStudent = sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudentName", TEST_TYPE, null, List.of());

        studentMySQLService.save(testStudent);

        Assertions.assertTrue(testStudent.getId() > 0);
        Assertions.assertTrue(studentMySQLService.getAll().size() > startingStudentCount);
    }

    private void initTEST_TYPE(Class<T> typeClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TEST_TYPE = typeClass.getDeclaredConstructor(String.class).newInstance("testTypeName");
    }

    private void prepareTestDB(MySqlClient dataClient) throws SQLException, URISyntaxException, IOException {
        String creationSql = Files.readString(new File(ITestBase.class.getResource("/sql/create_db.sql").toURI()).toPath());
        String deletionSql = Files.readString(new File(ITestBase.class.getResource("/sql/drop_db.sql").toURI()).toPath());


        System.out.println("Dropping all tables");
        dataClient.executeSql(deletionSql.split(";"));

        System.out.println("Creating tables");
        dataClient.executeSql(creationSql.split(";"));
    }
}
