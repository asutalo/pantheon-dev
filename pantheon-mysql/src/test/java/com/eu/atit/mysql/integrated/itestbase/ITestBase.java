package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.google.inject.TypeLiteral;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ITestBase {
    Map<Class<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();

    String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";
    LinkedList<String> dbParams = new LinkedList<>(List.of("student_service_test", "student_service_test", "devtestuser", "student_service_test@localhost"));

    MySqlClient dataClient = new MySqlClient(new Connector(driver(), JDBC_ROOT_URL, dbParams));

    MySQLServiceProvider mySQLServiceProvider = new MySQLServiceProvider(dataClient);

    default void prepDb() throws SQLException, URISyntaxException, IOException {
        prepareTestDB();
    }

    default <X> void initMySqlService(Class<X> forClass) {
        if (!mySQLServiceMap.containsKey(forClass)) {
            MySQLService<X> mySqlService = (MySQLService<X>) mySQLServiceProvider.provide(TypeLiteral.get(forClass));
            mySQLServiceMap.put(forClass, mySqlService);
        }
    }

    default <X> List<X> getAll(Class<X> ofClass) throws SQLException {
        return mySQLService(ofClass).getAll();
    }

    default <X> void insert(X toInsert, Class<X> ofClass) throws SQLException {
        mySQLService(ofClass).save(toInsert);
    }

    default <X> void update(X toUpdate, Class<X> ofClass) throws SQLException {
        mySQLService(ofClass).update(toUpdate);
    }


    void instanceOfT_shouldConvertMapToInstanceOfModel();

    void filteredSelect_provideBasicQueryBuilder();

    void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    void update_shouldUpdateExistingSpecificRecord();

    void delete_shouldDeleteSpecificRecord();

    void get_shouldFetchSpecificRecord_withQueryBuilder();

    void get_shouldSpecificRecord_withFilter();

    void getAll_shouldFetchAllRecords();

    void getAll_shouldFetchAllRecords_withQueryBuilder();

    void getAll_shouldFetchAllRecords_withFilter();

    private static <X> MySQLService<X> mySQLService(Class<X> ofClass) {
        return (MySQLService<X>) mySQLServiceMap.get(ofClass);
    }

    private static Driver driver() {
        try {
            return DriverManager.getDriver(JDBC_ROOT_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTestDB() throws SQLException, URISyntaxException, IOException {
        String creationSql = Files.readString(new File(Objects.requireNonNull(ITestBase.class.getResource("/sql/create_db.sql")).toURI()).toPath());
        String deletionSql = Files.readString(new File(Objects.requireNonNull(ITestBase.class.getResource("/sql/drop_db.sql")).toURI()).toPath());


        System.out.println("Dropping all tables");
        dataClient.executeSql(deletionSql.split(";"));

        System.out.println("Creating tables");
        dataClient.executeSql(creationSql.split(";"));
    }
}
