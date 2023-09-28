# testcontainers-java-extras 

Additional TestContainer containers.

#### Update (2023-09-26)

I've gotten useful feedback from the TestContainer developers - I should be able to add tests consistent
with all other TestContainers soon.

## SAP HANA Database (extends JdbcDatabase)

SAP HANA, express edition is a streamlined version of the SAP HANA platform which enables developers to jumpstart
application development in the cloud or personal computer to build and deploy modern applications that use up to
32GB memory. SAP HANA, express edition includes the in-memory data engine with advanced analytical data processing
engines for business, text, spatial, and graph data - supporting multiple data models on a single copy of the data.

The software license allows for both non-production and production use cases, enabling you to quickly prototype,
demo, and deploy next-generation applications using SAP HANA, express edition without incurring any license fees.
Memory capacity increases beyond 32GB are available for purchase at the SAP Store.

JDBC driver: `com.sap.cloud.db.jdbc:ngdbc:2.17.12`

## Vertica Database (extends JdbcDatabase)

Vertica is a unified analytics platform, based on a massively scalable architecture with the broadest set of
nalytical functions spanning event and time series, pattern matching, geospatial and end-to-end in-database
machine learning. Vertica enables you to easily apply these powerful functions to the largest and most demanding
analytical workloads, arming you and your customers with predictive business insights faster than any analytics
ata warehouse in the market. Vertica provides a unified analytics platform across major public clouds and on-premises
data centers and integrates data in cloud object storage and HDFS without forcing you to move any of your data.

JDBC driver: `com.vertica.jdbc:vertica-jdbc:22.3.0-0`

## SQLite Database (extends JdbcDatabase)

SQLite is a popular embedded database - it can't be a traditional Container since it can only use
host resources. However it's so popular on simple webapps, e.g., [Datasette](https://datasette.io/)
that I felt it was worth creating a psuedo-Container so developers will have access to the
broader TestContainer ecosystem.

JDBC driver: `org.xerial:sqlite-jdbc:3.42.0.0`

## Limitations

### No tests due to absent :jdbc-test

There are no tests yet since I've been unable to find `org.testcontainers:jdbc-test:1.18.3` in a
public repo. I can't build it locally on Ubuntu LTS but will build it later in a docker container.

### Failed login on SAP HANA database

The SAP HANA container requires the addition of a .json file containing the master password.
I'm creating that file but haven't found a way to set the ownership and permissions on it.

I suspect the solution will involve creating a separate 'data' volume and adding that file
to the new volume.
