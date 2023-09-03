package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseDiploma;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public class ITestBaseDiploma<D extends BaseDiploma<S>, S, T> implements ITestBase {
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

        ITestBase.insert(TEST_TYPE, tClass);
    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        S testStudent = getS("testStudent");
        ITestBase.insert(testStudent, sClass);

        ITestBase.insertTest(getD(testStudent, false), dClass, testStudent);
    }

    @Override
    public void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
//        ITestBase.deleteTest(getD("toBeDeleted"), dClass);
    }

    @Override
    public void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String startingName = "startingName";
        String updatedName = "updatedName";
//        ITestBase.updateTest(getD(startingName), dClass, updatedName);
    }

    @Override
    public void getAll_shouldFetchAllRecords() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException, URISyntaxException, IOException {
//        ITestBase.getAllTest(List.of(getD("first"), getD("second"), getD("third")), dClass);
    }

    private D getD(S student, Boolean obtained) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return dClass.getDeclaredConstructor(sClass, Boolean.class).newInstance(student, obtained);
    }

    private S getS(String name) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance(name, TEST_TYPE, null, List.of());
    }

    private void initTEST_TYPE(Class<T> typeClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TEST_TYPE = typeClass.getDeclaredConstructor(String.class).newInstance("testTypeName");
    }
}
