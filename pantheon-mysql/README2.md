# Pantheon-MySQL Module

The *Pantheon-MySQL* module is a streamlined library for working with MySQL databases. It provides modular, reusable
components that simplify database interactions. This includes managing database connections, executing queries, and
building dynamic SQL statements programmatically.

---

## Key Components

### `MySQLServiceProvider`

The `MySQLServiceProvider` acts as the central hub for managing and configuring MySQL services. It is responsible for
initializing, configuring, and providing access to the `MySQLService`.

This provider ensures that database interactions are properly initialized and offers dependency management for the other
components in the module.

Usage:

```java
MySQLServiceProvider serviceProvider = new MySQLServiceProvider();
MySQLService mySQLService = serviceProvider.getService();
```

---

### `MySQLService`

The `MySQLService` is the main interface used to interact with the MySQL database. It is built on top of the lower-level
`MySqlClient` and provides a convenient abstraction layer for executing queries, managing connections, and performing
database operations.

#### Methods in `MySQLService`:

1. **`connect()`**: Opens a connection to the MySQL database.
2. **`closeConnection()`**: Closes the active database connection.
3. **`executeQuery(String query)`**: Executes a SQL query and returns a `ResultSet`.
4. **`executeUpdate(String query)`**: Executes an SQL update statement (e.g., `INSERT`, `UPDATE`, `DELETE`).
5. **`prepareStatement(String query)`**: Returns a `PreparedStatement` for executing parametrized queries.
6. **`isConnected()`**: Checks if the database connection is active.

Relation to `MySQLServiceProvider`:
`MySQLService` is supplied and managed by the `MySQLServiceProvider`. The provider ensures that the service is properly
configured and can be reused within the application.

#### Example: Retrieving Data

```java
MySQLServiceProvider serviceProvider = new MySQLServiceProvider();
MySQLService service = serviceProvider.getService();

try{
        service.

connect();

ResultSet resultSet = service.executeQuery("SELECT id, username FROM users WHERE status = 'active'");
    
    while(resultSet.

next()){
        System.out.

println("ID: "+resultSet.getInt("id"));
        System.out.

println("Username: "+resultSet.getString("username"));
        }
        }finally{
        service.

closeConnection();
}
```

---

### `MySqlClient`

The `MySqlClient` is a low-level utility class responsible for direct interaction with the MySQL database. While
`MySQLService` provides a simplified API for developers, `MySqlClient` is the foundational engine upon which the service
operates. Advanced users can use `MySqlClient` for fine-grained database control or managing custom operations.

---

### `QueryBuilder`

The `QueryBuilder` is a utility for dynamically building SQL queries. It provides a clean and programmatic way to
construct complex SQL statements without manually handling string concatenation or syntax issues.

#### Example: Building a Query

```java
QueryBuilder queryBuilder = new QueryBuilder()
        .select("id", "username", "email")
        .from("users")
        .where("status = ?")
        .orderBy("id DESC");

String sql = queryBuilder.build();
System.out.

println(sql);
// Output: SELECT id, username, email FROM users WHERE status = ? ORDER BY id DESC
```

The `QueryBuilder` is especially useful when constructing queries based on dynamic conditions or user input.

---

## Code Examples

### Inserting Data into the Database

This example demonstrates how to insert a new record using `PreparedStatement` for safer, parametrized queries.

```java
MySQLServiceProvider serviceProvider = new MySQLServiceProvider();
MySQLService service = serviceProvider.getService();

try{
        service.

connect();

String insertSQL = "INSERT INTO users (username, password, status) VALUES (?, ?, ?)";
PreparedStatement preparedStatement = service.prepareStatement(insertSQL);
    preparedStatement.

setString(1,"johndoe");
    preparedStatement.

setString(2,"securepassword");
    preparedStatement.

setString(3,"active");

int rowsInserted = preparedStatement.executeUpdate();
    System.out.

println(rowsInserted +" row(s) inserted.");
}finally{
        service.

closeConnection();
}
```

---

### Working with Queries Dynamically

This example combines the `QueryBuilder` with `MySQLService` to perform a dynamic database query.

```java
MySQLServiceProvider serviceProvider = new MySQLServiceProvider();
MySQLService service = serviceProvider.getService();

try{
        service.

connect();

QueryBuilder builder = new QueryBuilder()
        .select("username", "email")
        .from("users")
        .where("status = 'active'")
        .orderBy("username ASC");

ResultSet resultSet = service.executeQuery(builder.build());
    while(resultSet.

next()){
        System.out.

println("Username: "+resultSet.getString("username"));
        System.out.

println("Email: "+resultSet.getString("email"));
        }
        }finally{
        service.

closeConnection();
}
```

---

### Updating Records in the Database

```java
MySQLServiceProvider serviceProvider = new MySQLServiceProvider();
MySQLService service = serviceProvider.getService();

try {
    service.connect();
    String updateSQL = "UPDATE users SET status = ? WHERE username = ?";
    PreparedStatement preparedStatement = service.prepareStatement(updateSQL);
    preparedStatement.setString(1, "inactive");
    preparedStatement.setString(2, "johndoe");

    int rowsUpdated = preparedStatement.executeUpdate();
    System.out.println(rowsUpdated + " row(s) updated.");
} finally {
    service.closeConnection();
}
```

---

### Deleting Records

```java
MySQLServiceProvider serviceProvider = new MySQLServiceProvider();
MySQLService service = serviceProvider.getService();

try {
    service.connect();
    String deleteSQL = "DELETE FROM users WHERE status = ?";
    PreparedStatement preparedStatement = service.prepareStatement(deleteSQL);
    preparedStatement.setString(1, "inactive");

    int rowsDeleted = preparedStatement.executeUpdate();
    System.out.println(rowsDeleted + " row(s) deleted.");
} finally {
    service.closeConnection();
}
```

---

## Summary

The *Pantheon-MySQL* module offers a robust, flexible, and developer-friendly toolset for interacting with MySQL
databases. By utilizing components such as `MySQLServiceProvider`, `MySQLService`, `MySqlClient`, and `QueryBuilder`,
developers can perform everything from simple queries to dynamic SQL generation with ease.

To get started, include this module in your project and initialize your `MySQLServiceProvider`. The entire framework is
designed to make database management simpler, safer, and more intuitive.