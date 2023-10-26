package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.mysql.service.annotations.MySqlField;
import com.eu.atit.mysql.service.annotations.Table;
import com.eu.atit.pantheon.annotation.data.Nested;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.eu.atit.mysql.integrated.itestbase.ITestBase.*;

public class ITestBaseCourse<C extends BaseCourse> implements ITestBase {
    Class<C> cClass;


    public void setUp(Class<C> courseClass) throws SQLException, URISyntaxException, IOException {
        prepDb();

        cClass = courseClass;

        initMySqlService(cClass);
    }

    @Override
    public void filteredSelect_provideBasicQueryBuilder() {
        basicFilteredSelectTest(cClass);
    }

    @Override
    public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        insertTest(getC(), cClass);
    }

    @Override
    public void delete_shouldDeleteSpecificRecord() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        deleteTest(getC("toBeDeleted"), cClass);
    }

    @Override
    public void update_shouldUpdateExistingSpecificRecord() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String startingName = "startingName";
        String updatedName = "updatedName";
        updateTest(getC(startingName), cClass, updatedName);
    }

    @Override
    public void getAll_shouldFetchAllRecords() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        getAllTest(List.of(getC("first"), getC("second"), getC("third")), cClass);
    }

    private C getC() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return getC("testClassName");
    }

    private C getC(String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return cClass.getDeclaredConstructor(String.class).newInstance(name);
    }
}
