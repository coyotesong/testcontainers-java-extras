# SQLite Module

See [Database containers](./index.md) for documentation and usage that is common to all relational database container types.

## What is SQLite?

[SQLite](https://www.sqlite.org/index.html) is a popular "embedded" database with small webapps, e.g.,
[Datasette](https://datasette.io/). It can be accessed via JDBC but is strictly local and is not a natural fit
for TestContainers.

I felt it worth creating a "container" for it anyway since this database is widely used
and there's a growing ecosystem of test resources that build on top of TestContainers
containers. Having this "container" allows the developer to use those resources.

In order to function an **alpine** container is created but not used.

### Resources

- [SQLite](https://www.sqlite.org/index.html)
- [SQLite Tutorial](https://sqlitetutorial.net)
- [SQLite Java](https://www.sqlitetutorial.net/sqlite-java/)


## How to install SQLite

### Debian/Ubuntu

```shell
sudo apt install sqlite3 sqlite3-tools
```

### Redhat

```shell
sudo yum install sqlite3
```

### Macs

SQLite is pre-installed on Macs.

### Windows

SQLite can be downloaded from [the SQLite download page](https://www.sqlite.org/download.html).

## Connect via JDBC

### Connecting to an in-memory database

```
jdbc:sqlite::memory:
```

H2 also supports in-memory databases that can be backed by a temporary file but
I don't know if SQLite supports this.

### Connecting to a disk-based database

```
jdbc:sqlite:/path/to/database.db
```

The path can be relative or absolute. The database will be created if necessary,
or you can create it manually using

```shell
sqlite3 /opt/myapp/mydatabase.db
```

## License

SQLite is released under the public domain.

## Adding this module to your project dependencies

Add the following dependency to your `pom.xml`/`build.gradle` file:

=== "Gradle"
```groovy
testImplementation "com.coyotesong.testcontainers:sqlite:{{latest_version}}"
```
=== "Maven"
```xml
<dependency>
<groupId>com.coyotesong.testcontainers-java-extras</groupId>
<artifactId>sqlite</artifactId>
<version>{{latest_version}}</version>
<scope>test</scope>
</dependency>
```

!!! hint
Adding this Testcontainers library JAR will not automatically add a database driver JAR to your project. You should ensure that your project also has a suitable database driver as a dependency.

### JDBC Driver
```
org.xerial:sqlite-jdbc:3.42.0.0
```