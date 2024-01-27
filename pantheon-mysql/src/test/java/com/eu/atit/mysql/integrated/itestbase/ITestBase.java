package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.integrated.model.base.WithId;
import com.eu.atit.mysql.integrated.model.base.WithName;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.pantheon.annotation.data.Nested;
import com.google.inject.TypeLiteral;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ITestBase {
    Map<Class<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();

    String JDBC_ROOT_URL = "jdbc:mysql://localhost:3308/";
    //    for mac:
//    String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";
    LinkedList<String> dbParams = new LinkedList<>(List.of("test-db", "root", "root", "root@localhost"));

    MySqlClient dataClient = new MySqlClient(new Connector(driver(), JDBC_ROOT_URL, dbParams));

    MySQLServiceProvider mySQLServiceProvider = new MySQLServiceProvider(dataClient);

//    todo 2 copies of same method?
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

    default void getAll_shouldFetchAllRecords() throws SQLException {
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords_withQueryBuilder() throws SQLException {
        Assertions.fail("not implemented");
    }

    default void getAll_shouldFetchAllRecords_withFilter(){
        Assertions.fail("not implemented");
    }

    static <X extends WithId> String basicFilteredSelectQuery(Class<X> ofClass) {
        return mySQLService(ofClass).filteredSelect().buildQueryString();
    }


    //todo finish the generic method below to "simplify" tests
    static <X extends WithId> void basicFilteredSelectTest(Class<X> ofClass) {
        MySQLService<X> mySQLService = mySQLService(ofClass);
        String tableName = mySQLService.getTableName();
        List<FieldInfo> joins = new ArrayList<>();

        List<FieldInfo> fieldInfos = fieldInfos(ofClass, new ArrayList<>(), ofClass, null);
        Iterator<FieldInfo> fieldInfoIterator = fieldInfos.iterator();
        StringBuilder expectedQueryBuilder = select();
        while (fieldInfoIterator.hasNext()){
            FieldInfo fieldInfo = fieldInfoIterator.next();
            if(fieldInfo.selectable()){
                selectField(expectedQueryBuilder, lowercase(fieldInfo.tableName()), lowercase(fieldInfo.fieldName()));
                if(fieldInfoIterator.hasNext()){
                    commaNewLine(expectedQueryBuilder);
                }
            }

            if(fieldInfo.joinedOn()){
                joins.add(fieldInfo);
            }

            if(!fieldInfoIterator.hasNext()){
                int lastIndex = expectedQueryBuilder.lastIndexOf(",");
                if(lastIndex == expectedQueryBuilder.length()-2){
                    expectedQueryBuilder.delete(lastIndex, expectedQueryBuilder.length());
                }
            }
        }
        from(tableName, expectedQueryBuilder);

        for (FieldInfo fieldInfo : joins) {
            if(fieldInfo.childJoin() == null)
                join(fieldInfo, expectedQueryBuilder);
            else {
                System.out.println(fieldInfo);
                System.out.println(fieldInfo.childJoin());
            }
        }

        expectedQueryBuilder.append(";");

        Assertions.assertEquals(expectedQueryBuilder.toString(), mySQLService.filteredSelect().buildQueryString());
//        Assertions.assertTrue(queryBuilder.getQueryParts().isEmpty());
    }

    private static void join(FieldInfo fieldInfo, StringBuilder stringBuilder) {
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("LEFT JOIN ");
        stringBuilder.append(fieldInfo.tableName());
        stringBuilder.append(" AS ");
        stringBuilder.append(fieldInfo.tableName().toLowerCase());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("\t\t\tON ");
        stringBuilder.append(fieldInfo.joinedFromTable().toLowerCase());
        stringBuilder.append(".");
        stringBuilder.append(fieldInfo.joinedFromField());
        stringBuilder.append(" = ");
        stringBuilder.append(fieldInfo.tableName().toLowerCase());
        stringBuilder.append(".");
        stringBuilder.append(fieldInfo.joinedOnField());
    }

    private static String lowercase(String s) {
        return s.toLowerCase();
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

        Assertions.assertEquals(getAll(ofClass), mySQLService(ofClass).getAll(mySQLService(ofClass).filteredSelect()));
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

    private static List<FieldInfo> fieldInfos(Class<?> ofClass, ArrayList<Class<?>> observedClasses, Class<?> parentClass, Field nestedField) {
        if(observedClasses.contains(ofClass)){
            String fieldName = fieldName(nestedField, nestedField.getAnnotation(MySqlField.class));
            return List.of(new FieldInfo(mySQLService(parentClass).getTableName(), fieldName, true, mySQLService(ofClass).getTableName(), primaryFieldName(ofClass), primaryFieldName(parentClass), false, null));
        } else {
            observedClasses.add(ofClass);

            Field[] fields = ofClass.getDeclaredFields();
            String primaryFieldName = primaryFieldName(ofClass);
            List<FieldInfo> fieldInfos = new ArrayList<>();
            for (Field field : fields) {
                MySqlField mySqlField = field.getAnnotation(MySqlField.class);
                Nested nested = field.getAnnotation(Nested.class);
                if(nested != null || mySqlField != null) {
                    if (nested != null){
                        fieldInfos.addAll(fieldInfos(getNestedType(field), observedClasses, ofClass, field));
                    } else {
                        String fieldName = fieldName(field, mySqlField);
                        if(!ofClass.equals(parentClass) && fieldName.equalsIgnoreCase(primaryFieldName)){
                            if(isList(nestedField)){
//                                fieldInfos.add(new FieldInfo(mySQLService(ofClass).getTableName(), fieldName));
                                fieldInfos.add(new FieldInfo(mySQLService(ofClass).getTableName(), fieldName, true, mySQLService(parentClass).getTableName(), null, primaryFieldName(parentClass), true, new FieldInfo(mySQLService(parentClass).getTableName(), null)));

                            } else {
                                fieldInfos.add(new FieldInfo(mySQLService(ofClass).getTableName(), fieldName, true, mySQLService(parentClass).getTableName(), nestingFieldName(nestedField, primaryFieldName), primaryFieldName(parentClass), true, null));
                            }
                        } else {
                            fieldInfos.add(new FieldInfo(mySQLService(ofClass).getTableName(), fieldName));
                        }
                    }
                }
            }
//            LEFT JOIN Student_Course AS student_course
//            ON student.id = student_course.student_id
//            LEFT JOIN Course AS course
//            ON student_course.course_id = course.id;
            return fieldInfos;
        }
    }



    private static String primaryFieldName(Class<?> ofClass) {
        for (Field declaredField : ofClass.getDeclaredFields()) {
            MySqlField mySqlField = declaredField.getAnnotation(MySqlField.class);
            if(mySqlField!=null && mySqlField.primary()){
                Nested nested = declaredField.getAnnotation(Nested.class);
                if(nested == null) {
                    return fieldName(declaredField, mySqlField);
                } else {
                    if(!isList(declaredField)) {
                        return primaryFieldName(getNestedType(declaredField));
                    }
                }
            }
        }

        throw new AssertionFailedError("no adequate primary key found for: " + ofClass.getName());
    }

    private static boolean isList(Field nestedField) {
        Type genericType = nestedField.getGenericType();
        return genericType.getTypeName().contains("List");
    }

    private static Class<?> getNestedType(Field nestedField) {
        if(isList(nestedField)) {
            return (Class<?>) ((ParameterizedType) nestedField.getGenericType()).getActualTypeArguments()[0];
        }
        return nestedField.getType();
    }

    private static String nestingFieldName(Field field, String primaryFieldName) {
        MySqlField mySqlField = field.getAnnotation(MySqlField.class);
        if(mySqlField==null || StringUtils.isBlank(mySqlField.column())){
            return mySQLService(field.getType()).getTableName().toLowerCase() + "_" + primaryFieldName;
        }
        return fieldName(field, mySqlField);
    }
    private static String fieldName(Field field, MySqlField mySqlField) {
        String fieldName = field.getName();

        if(mySqlField!=null && StringUtils.isNotBlank(mySqlField.column())){
            fieldName = mySqlField.column();
        }
        return fieldName;
    }



    private static StringBuilder select() {
        return new StringBuilder("SELECT\t");
    }

    private static void commaNewLine(StringBuilder stringBuilder) {
        stringBuilder.append(",");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("\t\t");
    }

    private static void from(String tableName, StringBuilder stringBuilder) {
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("FROM\t");
        stringBuilder.append(tableName);
        stringBuilder.append(" AS ");
        stringBuilder.append(lowercase(tableName));
    }

    private static void selectField(StringBuilder stringBuilder, String tableName, String fieldName) {
        stringBuilder.append(tableName);
        stringBuilder.append(".");

        stringBuilder.append(fieldName);
        stringBuilder.append(" AS ");
        stringBuilder.append(tableName);
        stringBuilder.append("_");
        stringBuilder.append(fieldName);
    }

    private static <X extends WithId> List<Integer> insertAll(List<X> toInserts, Class<X> ofClass) throws SQLException {
        List<Integer> insertedIDs = new ArrayList<>();
        for (X toInsert : toInserts) {
            insert(toInsert, ofClass);
            insertedIDs.add(toInsert.getId());
        }
        return insertedIDs;
    }
}
