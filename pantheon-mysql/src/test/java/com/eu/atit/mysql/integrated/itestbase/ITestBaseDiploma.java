package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.integrated.model.base.BaseType;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static com.eu.atit.mysql.integrated.itestbase.ITestBase.*;

public class ITestBaseDiploma<D extends BaseDiploma, S extends BaseStudent, T extends BaseType> implements ITestBase {
    T TEST_TYPE;

    Class<D> dClass;
    Class<S> sClass;
    Class<T> tClass;


    public void setUp(Class<D> diplomaClass, Class<S> studentClass, Class<T> typeClass) throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        prepDb();

        dClass = diplomaClass;
        sClass = studentClass;
        tClass = typeClass;
        initTEST_TYPE(typeClass);

        initMySqlService(dClass);
        initMySqlService(sClass);
        initMySqlService(tClass);

        insert(TEST_TYPE, tClass);
    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        insertTest(getD(true), dClass);
    }

    @Override
    public void filteredSelect_provideBasicQueryBuilder() {
        Assertions.assertEquals("""
                SELECT	diploma.obtained AS diploma_obtained,
                		student.id AS student_id,
                		student.name AS student_name,
                		type.id AS type_id,
                		type.name AS type_name,
                		course.id AS course_id,
                		course.name AS course_name
                FROM	Diploma AS diploma
                LEFT JOIN Student AS student
                			ON diploma.id = student.id
                LEFT JOIN Type AS type
                			ON student.type_id = type.id
                LEFT JOIN Student_Course AS student_course
                			ON student.id = student_course.student_id
                LEFT JOIN Course AS course
                			ON student_course.course_id = course.id;""".trim().replace("\r", ""), basicFilteredSelectQuery(dClass).trim().replace("\r", ""));
    }

    @Override
    public void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        deleteTest(getD(true), dClass);
    }

    @Override
    public void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        boolean starting = false;
        boolean updated = true;
        D testDiploma = insertD(starting);

        testDiploma.setObtained(updated);

        update(testDiploma, dClass);

        List<D> matching = getAll(dClass).stream().filter(s -> s.getId() == testDiploma.getId()).toList();
        Assertions.assertEquals(1, matching.size());
        Assertions.assertEquals(matching.get(0).obtained(), updated);
    }

    @Override
    public void getAll_shouldFetchAllRecords() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        getAllTest(List.of(getD(true), getD(false), getD(true)), dClass);
    }

    private D getD(S student, Boolean obtained) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return dClass.getDeclaredConstructor(sClass, Boolean.class).newInstance(student, obtained);
    }

    private S getS() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudent", TEST_TYPE, null, List.of());
    }

    private void initTEST_TYPE(Class<T> typeClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TEST_TYPE = typeClass.getDeclaredConstructor(String.class).newInstance("testTypeName");
    }

    private D insertD(Boolean b) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
        D toInsert = getD(b);
        insert(toInsert, dClass);
        return toInsert;
    }

    private D getD(Boolean b) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
        S testStudent = getS();
        insert(testStudent, sClass);

        return getD(testStudent, b);
    }
}
