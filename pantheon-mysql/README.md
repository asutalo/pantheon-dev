# Pantheon MySQL

Started off as a utility library for building and executing queries. Over time it got expanded to integrate
with [Pantheon](https://github.com/asutalo/pantheon).

# MySQLService

The library provides a best effort service that facilitates basic CRUD requests to the DB based on annotations on the
domain object. This service is mainly used by the front end Pantheon libraries to translate your domain objects into
API endpoints but can also be accessed without other Pantheon libraries.

## Usage


| Method         | Params                      | Description                                                                                                                                                                                                  |
| -------------- | --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
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
type=deactivated
to get the ID of that particular type followed by fetching Users where accountType_id = deactivated_id.

The limited nesting support provided by the generic service allows for the same to be achieved by doing a get request
for Users
with a filter that states accountType.type = deactivated which will produce a single query utilising a join and a where
clause.

## Alternative

In case you'd like to use your own implementation you can use the `MySqlClient` directly in combination with the `QueryBuilder`.

# MySqlClient

The client provided by this library consumes the queries provided via the `QueryBuilder`. It supports execution of three types of operations:

* select
* insert
* other DML (i.e. update/delete)

Each of the executions uses the provided `QueryBuilder` to construct and execute a prepared statement.[](https://)
