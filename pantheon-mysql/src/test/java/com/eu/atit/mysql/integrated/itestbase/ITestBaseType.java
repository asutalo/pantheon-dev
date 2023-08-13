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

    @Override
    public void instanceOfT_shouldConvertMapToInstanceOfModel() {

    }

    @Override
    public void filteredSelect_provideBasicQueryBuilder() {

    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException{

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
