package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static com.eu.atit.mysql.integrated.itestbase.ITestBase.*;

public class ITestBaseType<T extends BaseType> implements ITestBase {
    Class<T> tClass;


    public void setUp(Class<T> typeClass) throws SQLException, URISyntaxException, IOException {
        prepDb();

        tClass = typeClass;

        initMySqlService(tClass);
    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        insertTest(getT(), tClass);
    }

    @Override
    public void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        deleteTest(getT("toBeDeleted"), tClass);
    }

    @Override
    public void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String startingName = "startingName";
        String updatedName = "updatedName";
        updateTest(getT(startingName), tClass, updatedName);
    }

    @Override
    public void getAll_shouldFetchAllRecords() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        getAllTest(List.of(getT("first"), getT("second"), getT("third")), tClass);
    }

    private T getT() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return getT("testTypeName");
    }

    private T getT(String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return tClass.getDeclaredConstructor(String.class).newInstance(name);
    }
}
