package com.eu.atit.student.service.examples;

import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import com.google.inject.TypeLiteral;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class WorkingWithMySQLService {
    private static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    private static final LinkedList<String> DB_PARAMS = new LinkedList<>(List.of("someDbSchema", "someDbUser", "someUserPass", "someDbUser@localhost"));

    private final MySQLServiceProvider mySQLServiceProvider;

    public WorkingWithMySQLService() throws SQLException {
        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, DB_PARAMS));
        mySQLServiceProvider = new MySQLServiceProvider(dataClient);
    }

    public WorkingWithMySQLService(MySqlClient dataClient) {
        mySQLServiceProvider = new MySQLServiceProvider(dataClient);
    }

    public List<?> getAll(Class<?> classToFetch) throws SQLException {
        MySQLService<?> studentMySQLService = mySQLServiceProvider.provide(TypeLiteral.get(classToFetch));
        return studentMySQLService.getAll();
    }
}
