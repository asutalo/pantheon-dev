# Pantheon MySQL

Started off as a utility library for building and executing queries. Over time, it grew into a quasi mySQL ORM extension
of [Pantheon](https://github.com/asutalo/pantheon) that can be combined with its frontend components or used on its own.

# Table of Contents

* [MySQLService](#MySQLService)
    * [Usage](#Usage)
        * [Instantiation](#Instantiation)
        * [Annotations](#Annotations)
        * [Code Example](#Code-Example)
    * [Nesting](#Nesting)
* [MySqlClient](#MySqlClient)
* [QueryBuilder](#QueryBuilder)
* [Alternative to MySqlService](#Alternative-to-MySqlService)
* [Example without transactions](#Example-without-transactions)
* [Example with transactions](#Example-with-transactions)

# MySQLService

The library provides a best effort service that facilitates basic CRUD requests to the DB based on annotations on the
domain object. This service is mainly used by the front end Pantheon libraries to translate your domain objects into
API endpoints but can also be accessed without other Pantheon libraries.

## Usage

| Method         | Params                      | Description                                                                                                                                                                                                  |
|----------------|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| get            | Map filter                  | The map parameter should contain columns which you want to filter on and the values you're filtering for. Will return a single object or throw an exception.                                                 |
| get            | QueryBuilder filter         | Executes the provided query from the builder and returns a single object or throws an exception. Essentially converts query results into the desired object.                                                 |
| getAll         |                             | Gets all objects of a given type from the DB.                                                                                                                                                                |
| getAll         | QueryBuilder filteredSelect | Gets all objects of a given type filtered using your provided query. Essentially converts query results into the desired object.                                                                             |
| getAll         | Map filter                  | The map parameter should contain columns which you want to filter on and the values you're filtering for. Returns all matched rows.                                                                          |
| save           | Object toSave               | Inserts the provided object and updates it to contain the value of the primary key. Allows SQL exceptions to be thrown by the server.                                                                        |
| update         | Object toUpdate             | Performs an update on the provided object. In the event of a failure to update throws an exception.                                                                                                          |
| delete         | Object toDelete             | Deletes the provided object from the DB. In case of a failure to delete it will throw an exception.                                                                                                          |
| filteredSelect |                             | Prepares the QueryBuilder for you to add in any filters you wish to use.                                                                                                                                     |
| instanceOfT    | Map values                  | Uses the provided map of values to create an instance of the requested type. Used mainly by the JSON component of Pantheon to convert POST/PUT/DELETE requests into an object that CRUD can be performed on. |

### Instantiation

In order to get an instance of the generic service, you need to instantiate `MySQLServiceProvider` and use it to provide
yourself with an instance of the `MySQLService` connected to the type of the object you're fetching from the DB. The
ServiceProvider will instantiate and initialise the generic MySQLService for you.

### Annotations

The way the generic MySQLService works is by reading your domain class and identifying appropriate annotations
describing each of the fields in it.

Supported annotations and their values:

* @MySqlField
    * MysqlType type - used to describe the mapping between Java variable types and what they actually are in the DB
      table. This is mandatory as it is required for PreparedStatements to work.
    * String column - defaults to `""`. This represents the name of the column in your table. If not specified then the
      MySQLService will use the name of the variable to fetch the value from DB.
    * boolean primary - defaults to `false`. Used to identify a primary key, It is mandatory to have one variable marked
      as primary key.
* @Nested
    * This annotation is actually provided by *Pantheon* itself, and is used to mark a field as a custom class,
      indicating a nested object is present.
    * boolean outward - defaults to `false`. Marks the relationship as outward, meaning that the nested object, or
      rather its table is being referenced by the parent object. For example a *User* table would have a foreign key
      *role_id* pointing to another table, *Role*. This is effectively a `N:1` relationship type.
    * boolean inward - defaults to `false`. An inward relationship means that the nested object actually contains a
      foreign key pointing to the parent class. For example a *Citizen* owns a *Driving License* but the *Driving
      License* table is the one that has the foreign key *citizen_id*. This is essentially representing a 1:1
      relationship.
    * String link - defaults to `""`. Used to provide a name of the foreign key. If not provided the MySQLService will
      default it to *TargetTable_primaryKey* where the PK is identified by the annotation mentioned above.
    * boolean eager - defaults to false. Specifies if the entire nested object should be fetched or just the column
      marked as primary key.

### Code Example

*Define the domain class:*

```java
package some.domain;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

public final class Type {
    @MySqlField(type = MysqlType.INT, primary = true)
    private Integer id;
    @MySqlField(type = MysqlType.VARCHAR)
    private String name;

    public Type(String name) {
        this.name = name;
    }

    //mandatory empty constructor
    private Type() {
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer id() {
        return id;
    }
}
```

*Sample Main class to interact with the object above:*

```java
import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.service.MySQLService;
import com.eu.atit.mysql.service.MySQLServiceProvider;
import some.domain.Type;
import com.google.inject.TypeLiteral;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("some_db_name", "some_db_user", "some_Password", "some_db_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));
        MySQLServiceProvider mySQLServiceProvider = new MySQLServiceProvider(dataClient);

        MySQLService<Type> typeMySQLService = (MySQLService<Type>) mySQLServiceProvider.provide(TypeLiteral.get(Type.class));

        //fetch all
        System.out.println(typeMySQLService.getAll());

        //create and insert new
        Type newType = new Type("testType");
        Type savedType = typeMySQLService.save(newType);
        System.out.println(typeMySQLService.getAll());

        //delete by reference
        typeMySQLService.delete(savedType);
        System.out.println(typeMySQLService.getAll());

        //create, insert new, and delete by filtering
        Type otherNewType = new Type("testTypeDeleteById");
        Type otherSavedType = typeMySQLService.save(otherNewType);
        System.out.println(typeMySQLService.getAll());

        typeMySQLService.delete(typeMySQLService.get(Map.of("id", otherSavedType.id())));
        System.out.println(typeMySQLService.getAll());
    }
}
```

## Nesting

The generic service supports fetching with nested objects only. It is not possible to insert or update nested objects
by submitting a request to the parent object.

Fetching nested objects is provided mainly to allow for quick means of filtering the parent object based on attributes
it contains downstream in a way that requires fewer calls to the DB.

For example fetching all Users who have deactivated their accounts could be done by fetching AccountType where
type=deactivated to get the ID of that particular type followed by fetching Users where accountType_id = deactivated_id.

The limited nesting support provided by the generic service allows for the same to be achieved by doing a get request
for Users with a filter that states accountType.type = deactivated which will produce a single query utilising a join
and a where clause.

Fetching nested objects is by default ***lazy*** and will only fetch the *primary key* values. Eager fetching is
configurable but `not recommended` unless you are certain that you will not be fetching *many* objects as it might/will
lead to memory issues, especially when fetching a N:N relationship.

# MySqlClient

The client provided by this library consumes the queries provided via the `QueryBuilder`. It supports execution of three
types of operations:

* select
* insert
* other DML (i.e. update/delete)

Each of the executions uses the provided `QueryBuilder` to construct and execute a prepared statement.

Additionally, it allows you to start, finish, and rollback transactions if needed. Starting a transaction provides you
with an instance of a `Connection` that can be passed into the overloads of the methods above.

# QueryBuilder

The `QueryBuilder` allows you to construct SQL queries in a way that is easy to unit test. It supports:

* select all, select with column aliasing, insert, update, and delete statements
* filtering using **and** keyword
* `FROM` and `JOIN` keywords will use the provided table name to alias the table itself and allow you to alias the
  columns you are selecting where needed, i.e. SELECT tableX.id FROM tableX `tableX`

Most importantly, it provides you with the actual query by creating a `PreparedStatement` which should offer basic
protection from SQLInjection.

# Alternative to MySqlService

In case you'd like to use your own service implementation you can use the `MySqlClient` directly in combination with
the `QueryBuilder`, both of which are described above.

## Example without transactions

```java
import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.mysql.cj.MysqlType;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MainQueryBuilder {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("some_db_name", "some_db_user", "some_Password", "some_db_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));

        System.out.println("selected all: " + dataClient.executeSelectQuery(selectAll()));

        int insertedId = dataClient.executeInsertQuery(insertNewElement());
        System.out.println("inserted new element with id:" + insertedId);
        System.out.println("selected all after insert: " + dataClient.executeSelectQuery(selectAll()));

        dataClient.executeOtherDmlQuery(deleteById(insertedId));
        System.out.println("selected all after delete: " + dataClient.executeSelectQuery(selectAll()));
    }

    private static QueryBuilder deleteById(int insertedId) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from("type");
        queryBuilder.where();
        queryBuilder.keyIsVal(new MySqlValue(MysqlType.INT, "id", insertedId));
        return queryBuilder;
    }

    private static QueryBuilder insertNewElement() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert("type", new LinkedList<>(List.of(new MySqlValue(MysqlType.VARCHAR, "name", "newType"))));
        return queryBuilder;
    }

    private static QueryBuilder selectAll() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from("type");
        return queryBuilder;
    }
}
```

## Example with transactions

```java
import com.eu.atit.mysql.client.Connector;
import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.MySqlValue;
import com.eu.atit.mysql.query.QueryBuilder;
import com.mysql.cj.MysqlType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MainQueryBuilderTransactionary {
    static final String JDBC_ROOT_URL = "jdbc:mysql://localhost:3306/";

    public static void main(String[] args) throws SQLException {
        LinkedList<String> dbParams = new LinkedList<>(List.of("some_db_name", "some_db_user", "some_Password", "some_db_user@localhost"));

        MySqlClient dataClient = new MySqlClient(new Connector(DriverManager.getDriver(JDBC_ROOT_URL), JDBC_ROOT_URL, dbParams));

        Connection connection = dataClient.startTransaction();

        System.out.println("selected all: " + dataClient.executeSelectQuery(selectAll()));

        int insertedId = dataClient.executeInsertQuery(insertNewElement(), connection);
        System.out.println("inserted new element with id:" + insertedId);

        //despite the new insert ID being created, as we haven't ended the transaction the new element is not present in DB
        System.out.println("selected all after insert during transaction: " + dataClient.executeSelectQuery(selectAll()));

        dataClient.endTransaction(connection);

        //the transaction has been ended so new element is now present in DB
        System.out.println("selected all after ending transaction: " + dataClient.executeSelectQuery(selectAll()));

        //we're deleting the new element in regular fashion as we no longer
        dataClient.executeOtherDmlQuery(deleteById(insertedId));
        System.out.println("selected all after delete: " + dataClient.executeSelectQuery(selectAll()));

        //creating a brand new Connection for a new transaction
        connection = dataClient.startTransaction();

        //adding two new elements
        insertedId = dataClient.executeInsertQuery(insertNewElement(), connection);
        System.out.println("inserted new element with id:" + insertedId);

        insertedId = dataClient.executeInsertQuery(insertNewElement(), connection);
        System.out.println("inserted new element with id:" + insertedId);

        //deleting one of the new elements inside transaction
        dataClient.executeOtherDmlQuery(deleteById(insertedId), connection);

        //finally rolling back the addition and removal of new elements
        dataClient.rollbackTransaction(connection);
        dataClient.endTransaction(connection);

        //due to rollback we should not see the one new element that was inserted and not deleted
        System.out.println("selected all after after rollback: " + dataClient.executeSelectQuery(selectAll()));
    }

    private static QueryBuilder deleteById(int insertedId) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from("type");
        queryBuilder.where();
        queryBuilder.keyIsVal(new MySqlValue(MysqlType.INT, "id", insertedId));
        return queryBuilder;
    }

    private static QueryBuilder insertNewElement() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert("type", new LinkedList<>(List.of(new MySqlValue(MysqlType.VARCHAR, "name", UUID.randomUUID().toString()))));
        return queryBuilder;
    }

    private static QueryBuilder selectAll() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.selectAll();
        queryBuilder.from("type");
        return queryBuilder;
    }
}
```
