package org.testcontainers;

import org.testcontainers.utility.DockerImageName;

public interface SapHanaTestImages {
    DockerImageName SAPHANA_TEST_IMAGE = DockerImageName.parse("saplabs/hanaexpress:2.00.061.00.20220519.1");
}
