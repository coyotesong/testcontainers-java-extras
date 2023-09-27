#
# Create docker container that can build and test the TestContainer source code.
#
# Usage notes: you must make the host's docker socket available to this image:
#
#    docker .... -v /var/run/docker.sock:/var/run/docker.sock
#
FROM ubuntu:23.04

ARG JAVA_VERSION=17
ARG GRADLE_VERSION=8.3

#
# Set standard environment variables. We do not need to
# update PATH since we use 'update-alternatives' to handle
# gradle. This allows us to co-exist with the standard
# gradle package, if necessary, since we would only need
# to change GRADLE_HOME and select a different alternative.
#
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}
ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64

#
# Update /etc/apt information to include the most recent docker release
#
COPY docker.list /etc/apt/source.list.d/docker.list
COPY docker-keyring.gpg /usr/share/keyrings/docker-keyring.gpg

#
# Update pages, install git, java, maven, more
#
RUN /usr/bin/apt-get update -y && /usr/bin/apt-get upgrade -y
RUN /usr/bin/apt-get install -y git npm maven \
     openjdk-${JAVA_VERSION}-jdk openjdk-${JAVA_VERSION}-jdk-headless \
     zip unzip curl wget vim

#
# Download and install recent gradle
#
WORKDIR /tmp
RUN /usr/bin/wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip
RUN /usr/bin/wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip.sha256

# need to run 'unminimize' to run sha256sum

WORKDIR /opt
RUN /usr/bin/unzip /tmp/gradle-${GRADLE_VERSION}-bin.zip
RUN /usr/bin/ln -s gradle-${GRADLE_VERSION} gradle
RUN /usr/bin/update-alternatives --install /usr/bin/gradle gradle /opt/gradle-${GRADLE_VERSION}/bin/gradle 1000

#
# Download testcontainers-java source code
#
WORKDIR /usr/local/src
RUN /usr/bin/git clone https://github.com/testcontainers/testcontainers-java.git
WORKDIR /usr/local/src/testcontainers-java

#
# Build software
#
RUN gradle

#
# This must be add to settings.gradle - the tests require something
# not available in the docker image.
#
#
# file('modules').eachDir { dir ->
#   // modules where docker image fails to properly load
#   // azure: resource?
#   // db2: loooong wait
#   // elasticsearch : ip address 10.10....
#   if ( ! [ "azure", "db2", "elasticsearch", "gcloud" ].contains(dir.name) ) {
#       include dir.name
#       project(":${dir.name}").projectDir = dir
# }
#
# In addition the database modules fail with
#
#>   * What went wrong:
#>   A problem was found with the configuration of task ':mariadb:test' (type 'Test').
#>     - Gradle detected a problem with the following location: '/usr/local/src/testcontainers-java/modules/jdbc-test/build/libs/jdbc-test.jar'.
#> 
#>       Reason: Task ':mariadb:test' uses this output of task ':jdbc-test:shadowJar' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.
#>   
#>       Possible solutions:
#>         1. Declare task ':jdbc-test:shadowJar' as an input of ':mariadb:test'.
#>         2. Declare an explicit dependency on ':jdbc-test:shadowJar' from ':mariadb:test' using Task#dependsOn.
#>         3. Declare an explicit dependency on ':jdbc-test:shadowJar' from ':mariadb:test' using Task#mustRunAfter.
#>   
#>       For more information, please refer to https://docs.gradle.org/8.3/userguide/validation_problems.html#implicit_dependency in the Gradle documentation.
#
# But I've been unable to find the fix for this despite trying many different ideas.
#

# RUN gradle build
