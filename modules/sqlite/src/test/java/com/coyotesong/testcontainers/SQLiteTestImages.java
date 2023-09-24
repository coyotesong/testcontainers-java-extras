package com.coyotesong.testcontainers;

import org.testcontainers.utility.DockerImageName;

public interface SQLiteTestImages {
    DockerImageName SQLITE_TEST_IMAGE = DockerImageName.parse("alpine:latest");
}
