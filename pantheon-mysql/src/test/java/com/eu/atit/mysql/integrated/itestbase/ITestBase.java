package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.integrated.model.base.WithId;
import com.eu.atit.mysql.integrated.model.base.WithName;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.google.inject.TypeLiteral;
import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;

public interface ITestBase {
    Map<Class<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();

    String JDBC_ROOT_URL = "jdbc:mysql://localhost:3308/";
    //    for mac:
//    String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";
    LinkedList<String> dbParams = new LinkedList<>(List.of("test-db", "root", "root", "root@localhost"));

    MySqlClient dataClient = new MySqlClient(new Connector(driver(), JDBC_ROOT_URL, dbParams));

    MySQLServiceProvider mySQLServiceProvider = new MySQLServiceProvider(dataClient);

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

    static <X extends WithId> String basicFilteredSelectQuery(Class<X> ofClass) {
        return mySQLService(ofClass).filteredSelect().buildQueryString();
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

    static <X extends WithId> void getAllTest(List<X> toInserts, Class<X> ofClass) throws SQLException {
        int startingCount = getAll(ofClass).size();

        List<Integer> insertedIDs = insertAll(toInserts, ofClass);

        List<X> actualElements = getAll(ofClass);
        Assertions.assertEquals(toInserts.size(), actualElements.size() - startingCount);

        List<X> matching = actualElements.stream().filter(s -> insertedIDs.contains(s.getId())).toList();

        Assertions.assertEquals(toInserts.size(), matching.size());
        Assertions.assertEquals(insertedIDs, matching.stream().map(WithId::getId).toList());
    }

    static <X extends WithId> void getAllWithQueryBuilderTest(List<X> toInserts, Class<X> ofClass) throws SQLException {
        insertAll(toInserts, ofClass);
        MySQLService<X> xMySQLService = mySQLService(ofClass);
        Assertions.assertEquals(getAll(ofClass), xMySQLService.getAll(xMySQLService.filteredSelect()));
    }

    static <X extends WithId> void getAllWithFilterTest(List<X> toInserts, Class<X> ofClass) throws SQLException {
        getAllWithFilterTest(toInserts, ofClass, lowercaseTableNameOf(ofClass) + ".id");
    }

    static <X extends WithId> void getAllWithFilterTest(List<X> toInserts, Class<X> ofClass, String idColumn) throws SQLException {
        List<Integer> insertIds = insertAll(toInserts, ofClass);
        MySQLService<X> xMySQLService = mySQLService(ofClass);
        Assertions.assertEquals(getAll(ofClass).stream().filter(s -> s.getId() == insertIds.getFirst()).toList(), xMySQLService.getAll(Map.of(idColumn, insertIds.getFirst())));
    }

    static <X extends WithId> void getOneWithQueryBuilderTest(List<X> toInserts, Class<X> ofClass) throws SQLException {
        insertAllAndVerifyOne(toInserts, ofClass, (integer, xMySQLService) -> {
            QueryBuilder filteredSelect = xMySQLService.filteredSelect();
            filteredSelect.where();
            filteredSelect.keyIsVal(new MySqlValue(MysqlType.INT, lowercaseTableNameOf(ofClass) + ".id", integer));
            try {
                return xMySQLService.get(filteredSelect);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static <X extends WithId> String lowercaseTableNameOf(Class<X> ofClass) {
        return mySQLService(ofClass).getTableName().toLowerCase();
    }

    static <X extends WithId> void getOneWithFilterTest(List<X> toInserts, Class<X> ofClass) throws SQLException {
        getOneWithFilterTest(toInserts, ofClass, lowercaseTableNameOf(ofClass) + ".id");
    }

    static <X extends WithId> void getOneWithFilterTest(List<X> toInserts, Class<X> ofClass, String idColumn) throws SQLException {
        insertAllAndVerifyOne(toInserts, ofClass, (integer, xMySQLService) -> {
            try {
                return xMySQLService.get(Map.of(idColumn, integer));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <X extends WithId> void insertAllAndVerifyOne(List<X> toInserts, Class<X> ofClass, BiFunction<Integer, MySQLService<X>, X> getActual) throws SQLException {
        List<Integer> insertIds = insertAll(toInserts, ofClass);
        Integer expectedId = insertIds.getFirst();
        MySQLService<X> xMySQLService = mySQLService(ofClass);

        X actual = getActual.apply(expectedId, xMySQLService);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expectedId, actual.getId());
    }

    static <X extends WithId & WithName> void updateTest(X toUpdate, Class<X> ofClass, String updatedName) throws SQLException {
        insert(toUpdate, ofClass);
        toUpdate.setName(updatedName);

        update(toUpdate, ofClass);

        Assertions.assertTrue(toUpdate.getId() > 0);

        List<X> matching = getAll(ofClass).stream().filter(s -> s.getId() == toUpdate.getId()).toList();
        Assertions.assertEquals(1, matching.size());
        Assertions.assertEquals(matching.getFirst().getName(), updatedName);
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

    static <X> String toFieldName(Class<X> ofClass, String field) {
        return ofClass.getSimpleName().toLowerCase() + "_" + field;
    }

    static <X> X init(Class<X> ofClass, Map<String, Object> from) {
        return mySQLService(ofClass).instanceOfT(from);
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

    private static <X extends WithId> List<Integer> insertAll(List<X> toInserts, Class<X> ofClass) throws SQLException {
        List<Integer> insertedIDs = new ArrayList<>();
        for (X toInsert : toInserts) {
            insert(toInsert, ofClass);
            insertedIDs.add(toInsert.getId());
        }
        return insertedIDs;
    }

    default void prepDb() throws SQLException, URISyntaxException, IOException {
        String creationSql = Files.readString(new File(Objects.requireNonNull(ITestBase.class.getResource("/sql/create_db.sql")).toURI()).toPath());
        String deletionSql = Files.readString(new File(Objects.requireNonNull(ITestBase.class.getResource("/sql/drop_db.sql")).toURI()).toPath());


        System.out.println("Dropping all tables");
        dataClient.executeSql(deletionSql.split(";"));

        System.out.println("Creating tables");
        dataClient.executeSql(creationSql.split(";"));
    }

    default <X> void initMySqlService(Class<X> forClass) {
        if (!mySQLServiceMap.containsKey(forClass)) {
            MySQLService<X> mySqlService = (MySQLService<X>) mySQLServiceProvider.provide(TypeLiteral.get(forClass));
            mySQLServiceMap.put(forClass, mySqlService);
        }
    }

    default void instanceOfT_shouldConvertMapToInstanceOfModel() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Assertions.fail("not implemented");
    }

    default void filteredSelect_provideBasicQueryBuilder() {
        Assertions.fail("not implemented");
    }

    default void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Assertions.fail("not implemented");
    }

    default void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Assertions.fail("not implemented");
    }

    default void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
        Assertions.fail("not implemented");
    }

    default void get_shouldFetchSpecificRecord_withQueryBuilder() throws SQLException {
        Assertions.fail("not implemented");
    }

    default void get_shouldFetchSpecificRecord_withFilter() throws SQLException {
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords() throws SQLException {
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords_withQueryBuilder() throws SQLException {
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords_withFilter() throws SQLException {
        Assertions.fail("not implemented");
    }
}
