# Hamcrest Matchers (tentative)

[Hamcrest matchers](https://hamcrest.org/) provide a clean way to encapsulate test details. Perhaps more
importantly they provide a clean way to have a separate implementation of your test code. This eliminates
the temptation to use the code being tested to both populate and retrieve records. This approach is
tempting - it eliminates the need for a separate implementation - but is risky since it may approve
an implementation that's internally consistent but can't be used with anything else.

## Database Hamcrest Matchers

The potential database Hamcrest matchers will be

I hope the database matchers will be integrated into the JdbcContainer class. For now they're provided
via a JdbcMatchers interface so they can be easily added to any existing JDBC TestContainer.

```java
public interface JdbcMatchers {
    default Matcher tableExists(String tableName) { .... };

    default Matcher recordExists(String tableName, String query, Object... params) { ... };

    // default Matcher recordMatches(String tableName, StrictnessPolicy policy, String query, Object... params) { ... };
}
```

There will also be variants that can be used in the `assumeThat()` calls - they can be used to
prepopulate the test data.

None of the methods are difficult to implement - but it can be difficult to create a fluent
integration into the JUnit/Hamcrest tests. These matchers allow the details to be hidden so
the developer can focus on the essence of the tests.

### Usage

A typical usage would be

```java
private MyDatabaseContainer db = new MyDatabaseContainer(...);

public void testTableCreation() throws SQLException {
    final String TEST_TABLE_NAME = "test_123";
    assumeThat(not(db.tableExists(TEST_TABLE_NAME)));  // see above

    // call method that creates table

    assertThat(db.tableExists(TEST_TABLE_NAME));
}
```

with

```java
public class MyDatabaseContainer extends MSSQLContainer implements JdbcMatchers {
    // passthrough constructors, nothing else required
}
``` 