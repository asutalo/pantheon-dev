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

import static com.eu.atit.mysql.integrated.itestbase.ITestBase.*;

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

        S testStudent = insertS();
        Assertions.assertTrue(testStudent.getId() > 0);

        D testDiploma = dClass.getDeclaredConstructor(sClass, Boolean.class).newInstance(testStudent, Boolean.TRUE);

        insert(testDiploma, dClass);

        Assertions.assertTrue(getAll(sClass).size() > startingStudentCount);
    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        insertTest(getS(), sClass);
    }

    @Override
    public void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
        deleteTest(getS("toBeDeleted"), sClass);
    }

    @Override
    public void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String startingName = "startingName";
        String updatedName = "updatedName";
        updateTest(getS(startingName), sClass, updatedName);
    }

    @Override
    public void getAll_shouldFetchAllRecords() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        getAllTest(List.of(getS("first"), getS("second"), getS("third")), sClass);
    }

    private S insertS() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        S testStudent = sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudentName", TEST_TYPE, null, List.of());

        insert(testStudent, sClass);
        return testStudent;
    }

    private S getS(String name) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance(name, TEST_TYPE, null, List.of());
    }

    private S getS() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return getS("testStudentName");
    }
}
