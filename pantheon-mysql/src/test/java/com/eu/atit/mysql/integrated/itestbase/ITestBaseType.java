package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class ITestBaseType <T extends BaseType> implements ITestBase {
    Class<T> tClass;


    public void setUp(Class<T> typeClass) throws SQLException, URISyntaxException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        prepDb();

        tClass = typeClass;

        initMySqlService(tClass);
    }
}
