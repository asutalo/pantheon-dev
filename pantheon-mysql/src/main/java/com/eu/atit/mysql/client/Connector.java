package com.eu.atit.mysql.client;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

public class Connector {
    static final String USER_PROPERTY = "user";
    static final String PASSWORD_PROPERTY = "password";
    private final Driver jdbcDriver;
    private final String jdbcRootUrl;

    public final String dbName;
    private final String username;
    private final String pass;

    public Connector(Driver jdbcDriver, String jdbcRootUrl, LinkedList<String> dbParams) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcRootUrl = jdbcRootUrl;
        dbName = dbParams.get(0);
        username = dbParams.get(1);
        pass = dbParams.get(2);
    }

    public Connector(Driver jdbcDriver, String jdbcRootUrl, String dbName, String username, String pass) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcRootUrl = jdbcRootUrl;
        this.dbName = dbName;
        this.username = username;
        this.pass = pass;
    }

    public Connection connect() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(USER_PROPERTY, username);
        properties.setProperty(PASSWORD_PROPERTY, pass);

        return jdbcDriver.connect(
                jdbcRootUrl + dbName, properties);
    }

    public void close(Connection connection) throws SQLException {
        connection.close();
    }
}
