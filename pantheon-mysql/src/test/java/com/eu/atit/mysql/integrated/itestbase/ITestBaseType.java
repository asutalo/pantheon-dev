package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class ITestBaseType<T extends BaseType> implements ITestBase {
    Class<T> tClass;


    public void setUp(Class<T> typeClass) throws SQLException, URISyntaxException, IOException {
        prepDb();

        tClass = typeClass;

        initMySqlService(tClass);
    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ITestBase.insertTest(getT(), tClass);
    }

    private T getT() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return tClass.getDeclaredConstructor(String.class).newInstance("testTypeName");
    }
}
