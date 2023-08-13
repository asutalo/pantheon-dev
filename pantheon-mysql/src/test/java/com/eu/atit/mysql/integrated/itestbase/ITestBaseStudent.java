package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.integrated.model.base.BaseType;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public class ITestBaseStudent<S extends BaseStudent, T extends BaseType, D extends BaseDiploma, C extends BaseCourse> implements ITestBase {
    T TEST_TYPE;
    Class<S> sClass;
    Class<T> tClass;
    Class<D> dClass;
    Class<C> cClass;

    private void initTEST_TYPE(Class<T> typeClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TEST_TYPE = typeClass.getDeclaredConstructor(String.class).newInstance("testTypeName");
    }

    public void setUp(Class<S> studentClass, Class<T> typeClass, Class<D> diplomaClass, Class<C> courseClass) throws SQLException, URISyntaxException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        prepDb();

        sClass = studentClass;
        tClass = typeClass;
        dClass = diplomaClass;
        cClass = courseClass;
        initTEST_TYPE(typeClass);

        initMySqlService(sClass);
        initMySqlService(tClass);
        initMySqlService(dClass);
        initMySqlService(cClass);

        insert(TEST_TYPE, tClass);
    }

    public void shouldFetchStudentWithDiploma() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int startingStudentCount = getAll(sClass).size();

        S testStudent = sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudentName", TEST_TYPE, null, List.of());

        insert(testStudent, sClass);
        Assertions.assertTrue(testStudent.getId() > 0);

        D testDiploma = dClass.getDeclaredConstructor(sClass, Boolean.class).newInstance(testStudent, Boolean.TRUE);

        insert(testDiploma, dClass);

        Assertions.assertTrue(getAll(sClass).size() > startingStudentCount);
    }

    @Override
    public void instanceOfT_shouldConvertMapToInstanceOfModel() {

    }

    @Override
    public void filteredSelect_provideBasicQueryBuilder() {

    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int startingStudentCount = getAll(sClass).size();

        S testStudent = sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudentName", TEST_TYPE, null, List.of());

        insert(testStudent, sClass);

        Assertions.assertTrue(testStudent.getId() > 0);
        List<S> actualStudents = getAll(sClass);
        Assertions.assertTrue(actualStudents.size() > startingStudentCount);

        List<S> matchingStudents = actualStudents.stream().filter(s -> s.getId() == testStudent.getId()).toList();
        Assertions.assertEquals(1, matchingStudents.size());
        Assertions.assertNull(matchingStudents.get(0).getDiploma().obtained());
    }

    @Override
    public void update_shouldUpdateExistingSpecificRecord() {

    }

    @Override
    public void delete_shouldDeleteSpecificRecord() {

    }

    @Override
    public void get_shouldFetchSpecificRecord_withQueryBuilder() {

    }

    @Override
    public void get_shouldSpecificRecord_withFilter() {

    }

    @Override
    public void getAll_shouldFetchAllRecords() {

    }

    @Override
    public void getAll_shouldFetchAllRecords_withQueryBuilder() {

    }

    @Override
    public void getAll_shouldFetchAllRecords_withFilter() {

    }
}
