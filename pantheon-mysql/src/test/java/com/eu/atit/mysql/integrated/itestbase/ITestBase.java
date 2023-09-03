package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.integrated.model.base.WithId;
import com.eu.atit.mysql.integrated.model.base.WithName;
import com.eu.atit.mysql.integrated.model.base.WithNestedId;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

    static <X> List<X> getAll(Class<X> ofClass) throws SQLException {
        return mySQLService(ofClass).getAll();
    }

    static <X> void insert(X toInsert, Class<X> ofClass) throws SQLException {
        mySQLService(ofClass).save(toInsert);
    }

    static <X> void delete(X toDelete, Class<X> ofClass) throws SQLException {
        mySQLService(ofClass).delete(toDelete);
    }

    static <X> void update(X toUpdate, Class<X> ofClass) throws SQLException {
        mySQLService(ofClass).update(toUpdate);
    }


    default void instanceOfT_shouldConvertMapToInstanceOfModel(){
        Assertions.fail("not implemented");
    }

    default void filteredSelect_provideBasicQueryBuilder(){
        Assertions.fail("not implemented");
    }

    default void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException{
        Assertions.fail("not implemented");
    }

    default void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Assertions.fail("not implemented");
    }

    default void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
        Assertions.fail("not implemented");
    }

    default void get_shouldFetchSpecificRecord_withQueryBuilder(){
        Assertions.fail("not implemented");
    }

    default void get_shouldSpecificRecord_withFilter(){
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException, URISyntaxException, IOException {
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords_withQueryBuilder(){
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords_withFilter(){
        Assertions.fail("not implemented");
    }

    static <X extends WithId> void insertTest(X toInsert, Class<X> ofClass) throws SQLException {

        int startingCount = getAll(ofClass).size();

        insert(toInsert, ofClass);

        Assertions.assertTrue(toInsert.getId() > 0);
        List<X> actualElements = getAll(ofClass);
        Assertions.assertTrue(actualElements.size() > startingCount);

        List<X> matching = actualElements.stream().filter(s -> s.getId() == toInsert.getId()).toList();
        Assertions.assertEquals(1, matching.size());
    }

    static <X extends WithId> void getAllTest(List<X> toInserts, Class<X> ofClass) throws SQLException, URISyntaxException, IOException {
        int startingCount = getAll(ofClass).size();

        List<Integer> insertedIDs = new ArrayList<>();
        for (X toInsert : toInserts) {
            insert(toInsert, ofClass);
            insertedIDs.add(toInsert.getId());
        }

        List<X> actualElements = getAll(ofClass);
        Assertions.assertEquals(toInserts.size(), actualElements.size() - startingCount);

        List<X> matching = actualElements.stream().filter(s -> insertedIDs.contains(s.getId())).toList();

        Assertions.assertEquals(toInserts.size(), matching.size());
        Assertions.assertEquals(insertedIDs, matching.stream().map(WithId::getId).toList());
    }

    static <X extends WithId & WithName> void updateTest(X toUpdate, Class<X> ofClass, String updatedName) throws SQLException {
        insert(toUpdate, ofClass);
        toUpdate.setName(updatedName);

        update(toUpdate, ofClass);

        Assertions.assertTrue(toUpdate.getId() > 0);

        List<X> matching = getAll(ofClass).stream().filter(s -> s.getId() == toUpdate.getId()).toList();
        Assertions.assertEquals(1, matching.size());
        Assertions.assertEquals(matching.get(0).getName(), updatedName);
    }

    static <X extends WithId> void deleteTest(X toDelete, Class<X> ofClass) throws SQLException {
        insert(toDelete, ofClass);

        int startingCount = getAll(ofClass).size();
        Assertions.assertTrue(startingCount > 0);

        delete(toDelete, ofClass);
        List<X> actualElements = getAll(ofClass);
        Assertions.assertTrue(actualElements.size() < startingCount);
        List<X> matching = actualElements.stream().filter(s -> s.getId() == toDelete.getId()).toList();
        Assertions.assertEquals(0, matching.size());
    }

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

    private static void prepareTestDB() throws SQLException, URISyntaxException, IOException {
        String creationSql = Files.readString(new File(Objects.requireNonNull(ITestBase.class.getResource("/sql/create_db.sql")).toURI()).toPath());
        String deletionSql = Files.readString(new File(Objects.requireNonNull(ITestBase.class.getResource("/sql/drop_db.sql")).toURI()).toPath());


        System.out.println("Dropping all tables");
        dataClient.executeSql(deletionSql.split(";"));

        System.out.println("Creating tables");
        dataClient.executeSql(creationSql.split(";"));
    }
}
