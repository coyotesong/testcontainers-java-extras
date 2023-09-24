package com.coyotesong.testcontainer;

import org.testcontainers.utility.DockerImageName;

public interface H2TestImages {
    DockerImageName H2_TEST_IMAGE = DockerImageName.parse("alpine:latest");
}
