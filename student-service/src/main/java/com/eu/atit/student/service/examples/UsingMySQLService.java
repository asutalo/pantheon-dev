package com.eu.atit.student.service.examples;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.eu.atit.student.service.model.Diploma;
import com.eu.atit.student.service.model.Student;
import com.eu.atit.student.service.model.Type;
import com.google.inject.TypeLiteral;

import java.sql.SQLException;
import java.util.Map;

public class UsingMySQLService  {
    private final MySQLServiceProvider mySQLServiceProvider;

    public UsingMySQLService(MySqlClient mySqlClient) {
        mySQLServiceProvider = new MySQLServiceProvider(mySqlClient);
    }

    public void printAll(TypeLiteral<?> typeLiteral) throws SQLException {
        MySQLService<?> mySQLService = mySQLServiceProvider.provide(typeLiteral, true);
        mySQLService.getAll().forEach(System.out::println);
    }

    public void save(Object testType) throws SQLException {
        MySQLService mySQLService = mySQLServiceProvider.provide(TypeLiteral.get(testType.getClass()));

        mySQLService.save(testType);
    }

    public void update(Object newType) throws SQLException {
        MySQLService mySQLService = mySQLServiceProvider.provide(TypeLiteral.get(newType.getClass()));

        mySQLService.update(newType);
    }

    public void delete(Object newType) throws SQLException {
        MySQLService mySQLService = mySQLServiceProvider.provide(TypeLiteral.get(newType.getClass()));
        mySQLService.delete(newType);
    }

    public Object get(Map<String, Object> stringIntegerMap, TypeLiteral<?> typeLiteral) throws SQLException {
        MySQLService<?> mySQLService = mySQLServiceProvider.provide(typeLiteral, true);

        return mySQLService.get(stringIntegerMap);
    }
}
