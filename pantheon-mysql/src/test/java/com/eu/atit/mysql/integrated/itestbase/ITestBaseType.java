package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseType;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eu.atit.mysql.integrated.itestbase.ITestBase.*;

public class ITestBaseType<T extends BaseType> implements ITestBase {
    Class<T> tClass;
    List<T> someTs = new ArrayList<>();

    public void setUp(Class<T> typeClass) throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        prepDb();

        tClass = typeClass;

        initMySqlService(tClass);
        someTs.addAll(List.of(getT("first"), getT("second"), getT("third")));
    }

    @Override
    public void filteredSelect_provideBasicQueryBuilder() {
        Assertions.assertEquals("""
                SELECT	type.id AS type_id,
                		type.name AS type_name
                FROM	Type AS type;""".trim().replace("\r", ""), basicFilteredSelectQuery(tClass).trim().replace("\r", ""));
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
    public void getAll_shouldFetchAllRecords() throws SQLException {
        getAllTest(someTs, tClass);
    }

    @Override
    public void getAll_shouldFetchAllRecords_withQueryBuilder() throws SQLException {
        getAllWithQueryBuilderTest(someTs, tClass);
    }

    private T getT() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return getT("testTypeName");
    }

    private T getT(String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return tClass.getDeclaredConstructor(String.class).newInstance(name);
    }
}
