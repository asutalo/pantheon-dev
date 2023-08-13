package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.integrated.model.base.BaseCourse;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ITestBase<S extends BaseStudent, T extends BaseType, D extends BaseDiploma, C extends BaseCourse> {
    T TEST_TYPE;
    Class<S> sClass;
    Class<T> tClass;
    Class<D> dClass;
    Class<C> cClass;

    Map<Class<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();
    MySQLService<T> typeMySQLService;
    MySQLService<S> studentMySQLService;
    MySQLService<D> diplomaMySQLService;

    private static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final LinkedList<String> dbParams = new LinkedList<>(List.of("student_service_test", "student_service_test", "devtestuser", "student_service_test@localhost"));

    static MySQLServiceProvider mySQLServiceProvider;
    public void setUp(Class<S> studentClass, Class<T> typeClass, Class<D> diplomaClass, Class<C> courseClass) throws SQLException, URISyntaxException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));
        prepareTestDB(dataClient);
        mySQLServiceProvider = new MySQLServiceProvider(dataClient);
        sClass = studentClass;
        tClass = typeClass;
        dClass = diplomaClass;
        cClass = courseClass;
        initTEST_TYPE(typeClass);

        typeMySQLService = (MySQLService<T>) mySQLServiceProvider.provide(TypeLiteral.get(typeClass));
        studentMySQLService = (MySQLService<S>) mySQLServiceProvider.provide(TypeLiteral.get(studentClass));
        diplomaMySQLService = (MySQLService<D>) mySQLServiceProvider.provide(TypeLiteral.get(diplomaClass));

        mySQLServiceMap.put(typeClass, typeMySQLService);
        mySQLServiceMap.put(studentClass, studentMySQLService);
        mySQLServiceMap.put(diplomaClass, diplomaMySQLService);

        insert(TEST_TYPE, tClass);
    }

    <X> X insert(X toInsert, Class<X> insertionClass) throws SQLException {
        MySQLService<X> mySQLService = (MySQLService<X>) mySQLServiceMap.get(insertionClass);
        mySQLService.save(toInsert);

        return toInsert;
    }

    <X> void update(X toUpdate, Class<X> insertionClass) throws SQLException {
        MySQLService<X> mySQLService = (MySQLService<X>) mySQLServiceMap.get(insertionClass);
        mySQLService.update(toUpdate);
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
