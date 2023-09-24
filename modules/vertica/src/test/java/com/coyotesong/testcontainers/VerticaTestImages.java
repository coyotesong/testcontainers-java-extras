package com.coyotesong.testcontainers;

import org.testcontainers.utility.DockerImageName;

public interface VerticaTestImages {
    DockerImageName VERTICA_TEST_IMAGE = DockerImageName.parse("vertica/vertica-ce:23.3.0-0");
}
