# testcontainers-java-extras

This repository contains:
- JdbcContainer hamcrest matchers
- additional java TestContainers.

## Hamcrest Matchers (tentative)

[Hamcrest matchers](https://hamcrest.org/) provide a clean way to encapsulate test details. Perhaps more
importantly they provide a clean way to have a separate implementation of your test code. This eliminates
the temptation to use the code being tested to both populate and retrieve records. This approach is
tempting - it eliminates the need for a separate implementation - but is risky since it may approve
an implementation that's internally consistent but can't be used with anything else.

### Database Hamcrest Matchers

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

#### Usage

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

## New Database TestContainers

### Relational Database / Data Warehouse / Big Data

These are cloud-based Big Data / Data Warehouse / etc databases that also provide a docker-based
implementation for evaluation and testing. The new TestContainers work with the docker-based
implementation.

- [SAP HANA](https://www.sap.com/products/technology-platform/hana.html)
- [Teradata](https://www.teradata.com/) (tentative)
- [Vertica](https://www.vertica.com/)

### Relational Database / Embedded

These are widely used embedded databases.

Somewhat surprisingly both of these databases implement many advanced features, e.g., triggers,
recursive queries, and common expressions.

- [H2](https://h2database.com/)
  - Pros: client/server or embedded, easily extended with java-hased UDF and UDT
  - Cons: 
- [SQLite](https://www.sqlite.org/index.html)
  - Pros: extremely popular with docker-based apps like [Datasette](https://datasette.io/)
  - Cons: requires external software package (e.g., `sqlite3`), limited data types

## Other New TestContainers (tentative)

### Email (tentative)

This Container will provide access to SMTP, POP3, and IMAP services.

### Identity Providers (IdP) (tentative)

I do not plan to offer Microsoft Active Directory (AD) or SAMBA.

IPA / FreeIPA is a Linux-based IdP that can be used in place of MS AD. IPA is a commercial product
from RedHat, FreeIPA is an open-source subset of it. At the time of this writing it does not
officially support execution within a Docker container.

LDAP is somewhat interesting since it's often used as part of a broader Identity Provider (IdP).
At a minimum it should be integrated with a Kerberos KDC since that is a widely used combination.

LDAP can also use a relational database as the backend - this gives you the legacy flexibility
of LDAP with the development flexibility of a database.

LDAP is also often bundled with DNS since MS Active Directory publishes its resources via
DNS SRV records.

- OpenLDAP
- FreeIPA

