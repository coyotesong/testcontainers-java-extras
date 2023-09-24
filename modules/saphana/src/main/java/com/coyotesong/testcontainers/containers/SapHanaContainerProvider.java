package com.coyotesong.testcontainers.containers;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.JdbcDatabaseContainerProvider;
import org.testcontainers.utility.DockerImageName;

/**
 * Factory for SapHana containers.
 */
public class SapHanaContainerProvider extends JdbcDatabaseContainerProvider {

    @Override
    public boolean supports(String databaseType) {
        return databaseType.equals(SapHanaContainer.NAME);
    }

    @Override
    public JdbcDatabaseContainer newInstance() {
        return newInstance(SapHanaContainer.DEFAULT_TAG);
    }

    @Override
    public JdbcDatabaseContainer newInstance(String tag) {
        return new SapHanaContainer(DockerImageName.parse(SapHanaContainer.IMAGE).withTag(tag));
    }
}
