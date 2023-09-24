# Vertica Module

See [Database containers](./index.md) for documentation and usage that is common to all relational database container types.

_(The following is copied from [vertica/vertica-ce](https://hub.docker.com/r/vertica/vertica-ce))_

## What is Vertica?

Vertica is a unified analytics platform, based on a massively scalable architecture with the broadest set of
nalytical functions spanning event and time series, pattern matching, geospatial and end-to-end in-database
machine learning. Vertica enables you to easily apply these powerful functions to the largest and most demanding
analytical workloads, arming you and your customers with predictive business insights faster than any analytics
ata warehouse in the market. Vertica provides a unified analytics platform across major public clouds and on-premises
data centers and integrates data in cloud object storage and HDFS without forcing you to move any of your data.

## How to Use This Image

To simplify usage, this image provides the following configurations:

- [VMart](https://www.vertica.com/docs/latest/HTML/Content/Authoring/GettingStartedGuide/IntroducingVMart/IntroducingVMart.htm) example database
- [DBADMIN](https://www.vertica.com/docs/latest/HTML/Content/Authoring/AdministratorsGuide/DBUsersAndPrivileges/Roles/PredefinedRoles.htm) database user account
- verticadba database group
- Note: By default, there is no database password.

### Access the database with vsql or external client

The 5433 and 5444 ports are mapped to your host.

You can then access the database in one of the following ways:

- vsql on the container
- vsql on the host
- An external client using the 5433 and 5444 port

## Persistence

This container mounts a Docker volume named vertica-data to persist data for the Vertica database.
A Docker volume provides the following advantages over a mounted host directory:

- Cross-platform acceptance. Docker volumes are compatible with Linux, MacOS, and Microsoft Windows.
- The container runs with different username to user-id mappings. A container with a mounted host directory might create files that you cannot inspect or delete because they are owned by a user that is determined by the Docker daemon.
- Note: A Docker volume is represented on the host filesystem as a directory. These directories are created automatically and stored at /var/lib/docker/volumes/. Each volume is stored under `./volumename/_data/`. A small filesystem might might limit the amount of data you can store in your database.

### Bind mounts
As an alternative to a Docker volume, you can use a bind mount to persist data to another directory with sufficient disk space:

```sh
$ docker run -p 5433:5433 -p 5444:5444\
    --mount type=bind,source=/<directory>,target=/data \
    --name vertica_ce \
    vertica/vertica-ce
```
Important: The user that executes docker run must have read and write privileges on the source directory.

## License

View the [license information](https://www.microfocus.com/en-us/legal/software-licensing) for this image.

## Adding this module to your project dependencies

Add the following dependency to your `pom.xml`/`build.gradle` file:

=== "Gradle"
```groovy
testImplementation "com.coyotesong.testcontainers:vertica:{{latest_version}}"
```
=== "Maven"
```xml
<dependency>
<groupId>com.coyotesong.testcontainers-java-extras</groupId>
<artifactId>vertica</artifactId>
<version>{{latest_version}}</version>
<scope>test</scope>
</dependency>
```

!!! hint
Adding this Testcontainers library JAR will not automatically add a database driver JAR to your project. You should ensure that your project also has a suitable database driver as a dependency.

### JDBC Driver

```
com.vertica.jdbc:vertica-jdbc:22.3.0-0
```

