# SAP HANA Module

See [Database containers](./index.md) for documentation and usage that is common to all relational database container types.

Note: there is a sister docker image: [SAP HANA Express + XSA](https://hub.docker.com/r/saplabs/hanaexpressxsa).
This blog discusses its uses:
[XSA Blog Series – Basic Principles of SAP HANA XSA](https://blogs.sap.com/2023/07/13/xsa-blog-series-basic-principles-of-sap-hana-xsa/).

_(Copied from [saplabs/saphana](https://hub.docker.com/r/saplabs/hanaexpress))_

TEMPORARY NOTE - connections fail since I'm not setting the proper password ownership and permissions yet!

## What is SAP HANA

SAP HANA, express edition is a streamlined version of the SAP HANA platform which enables developers to jumpstart
application development in the cloud or personal computer to build and deploy modern applications that use up to
32GB memory. SAP HANA, express edition includes the in-memory data engine with advanced analytical data processing
engines for business, text, spatial, and graph data - supporting multiple data models on a single copy of the data.

The software license allows for both non-production and production use cases, enabling you to quickly prototype,
demo, and deploy next-generation applications using SAP HANA, express edition without incurring any license fees.
Memory capacity increases beyond 32GB are available for purchase at the SAP Store.

_Additional content_

A SAP HANA installation can support up to 100 separate _instances_. Each instance contains a _server database_
and one or more _tenant databases_. The default docker image uses an `instanceID` of 90 so the unmapped ports 
used are

- 39013 - SAP HANA control port
- 39017 - system database port
- 39041 - tenant database ports
- 59013 - SAP HANA control port
- 1128-1129 - Host agent (HTTP/HTTPS)
- 8000 - SAP HANA platform

The TCP/IP port of the _server_ database is well-defined and can be reliably
mapped by this Container.

The TCP/IP port of the _tenant databases_ uses a "first come, first served" approach. This isn't
a problem with the default docker image since it's running a single tenant but could cause problems if you
create a child docker image that contains multiple tenants.

### Host Requirements

Add this to `/etc/sysctl.conf` (and reboot?)

```
fs.file-max=20000000
fs.aio-max-nr=262144
vm.memory_failure_early_kill=1
vm.max_map_count=135217728
net.ipv4.ip_local_port_range=40000 60999
```

### Set up password for SAP HANA, express edition

To make your system more secure, you specify your own password before you create your container. This is done by
creating a json file as opposed to having a default password. The file can be stored locally or on another system
accessible by URL. If the file is to be stored locally, store it in the `/data/<directory_name>` directory.

You must then add permissions for this file to be readable by the hxeadm user in the container. Change permissions with:

```shell
sudo chmod 600 /data/<directory_name>/<file_name>.json
sudo chown 12000:79 /data/<directory_name>/<file_name>.json
```

### Test the container

When you are logged into the SAP HANA, express edition container, you can test your installation by entering the following:

```shell
whoami
```

You should be logged in as hxeadm, the default SAP HANA, express edition user.

You can also enter the following:

```shell
HDB info
```
And you should see the following services running:

```
hdbnameserver
hdbcompileserver
hdbdiserver
hdbwebdispatcher
```

### Test the database

You can log into the system database with the following command:

```shell
hdbsql -i 90 -d <system_database> -u SYSTEM -p <password>
```

You can log into your tenant database with the following command:

```shell
hdbsql -i 90 -d <tenant_database> -u SYSTEM -p <password>
```

#### Connect via JDBC driver

To log into your system database via JDBC, use the following command:

```
jdbc:sap://<ip_address>:39017/?databaseName=<database_name>
```

To log into your tenant database via JDBC, use the following command:

```
jdbc:sap://<ip_address>:39041/?databaseName=<tenant_name>
```

For detailed information on the connection properties you can specify when connecting using JDBC, see
[JDBC Connection Properties](https://help.sap.com/viewer/0eec0d68141541d1b07893a39944924e/latest/en-US/109397c2206a4ab2a5386d494f4cf75e.html)
in the SAP HANA Client Interface Programming Reference.

## Client Drivers

If you want to download clients to connect to your SAP HANA, express edition installation from another device,
you can download client drivers. First you will need to download the SAP HANA, express edition download manager
by registering online at the [SAP HANA, express edition registration page](https://www.sap.com/cmp/ft/crm-xu16-dat-hddedft/index.html.

For more information about downloading and installing clients for SAP HANA, express edition, see the 
[Install the SAP HANA, express edition](https://www.sap.com/developer/groups/hxe-install-clients.html) clients group tutorials.

### SAP HANA Studio

SAP HANA Studio is available as a plugin for Eclipse. To install the plugin, visit the tutorial How to download and
install the HANA Eclipse plugin. Note, there are specific instructions for connecting Docker installations.

### End User License Agreement

You can find the SAP End User License Agreement at [https://www.sap.com/docs/download/cmp/2016/06/sap-hana-express-dev-agmt-and-exhibit.pdf](https://www.sap.com/docs/download/cmp/2016/06/sap-hana-express-dev-agmt-and-exhibit.pdf).

### Tutorials

You can find this information as a [tutorial](https://www.sap.com/developer/tutorials/hxe-ua-install-using-docker.html) at sap.com.

### Creating a new container

```shell
sudo docker run -p 39013:39013 -p 39017:39017 -p 39041-39045:39041-39045 -p 1128-1129:1128-1129 -p 59013-59014:59013-59014 -v /data/<directory_name>:/hana/mounts \
      --ulimit nofile=1048576:1048576 \
      --sysctl kernel.shmmax=1073741824 \
      --sysctl net.ipv4.ip_local_port_range='40000 60999' \
      --sysctl kernel.shmmni=524288 \
      --sysctl kernel.shmall=8388608 \
      --name <container_name> \
    store/saplabs/hanaexpress:<tag> \
      --passwords-url <file://<path_to_json_file> OR http/https://<url_to_json_file>> \
      --agree-to-sap-license
```

Note: several of these properties can not be set via the Container.

## Adding this module to your project dependencies

Add the following dependency to your `pom.xml`/`build.gradle` file:

=== "Gradle"
```groovy
testImplementation "com.coyotesong.testcontainers:saphana:{{latest_version}}"
```
=== "Maven"
```xml
<dependency>
<groupId>com.coyotesong.testcontainers-java-extras</groupId>
<artifactId>saphana</artifactId>
<version>{{latest_version}}</version>
<scope>test</scope>
</dependency>
```

!!! hint
Adding this Testcontainers library JAR will not automatically add a database driver JAR to your project. You should ensure that your project also has a suitable database driver as a dependency.

### JDBC Driver
```
com.sap.cloud.db.jdbc:ngdbc:2.17.12
```