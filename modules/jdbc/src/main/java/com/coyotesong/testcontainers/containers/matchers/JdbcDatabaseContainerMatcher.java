package com.coyotesong.testcontainers.containers.matchers;

import org.hamcrest.Matcher;

/**
 * Interface that adds Hamcrest matchers to JdbcDatabaseContainers
 */
public interface JdbcDatabaseContainerMatcher<
    SELF extends org.testcontainers.containers.JdbcDatabaseContainer<SELF> & JdbcDatabaseContainerMatcher<SELF>
> {
    /**
     * Get 'this'.
     *
     * This can be dropped once these methods are integrated into
     * JdbcDatabaseContainer. Then we can just use 'this' in the
     * method calls.
     *
     * @return 'this' cast to JdbcDatabaseContainer
     */
    default SELF getContainer() {
        @SuppressWarnings("unchecked")
        final SELF container = (SELF) this;
        return container;
    }

    /**
     * Get TableExists matcher
     *
     * @param catalogName catalogName, may be null
     * @param schemaName schemaName, may be null
     * @param tableName tableName, may be null
     * @return Hamcrest Matcher
     */
    default Matcher<Void> tableExists(String catalogName, String schemaName, String tableName) {
        final Matcher<Void> matcher = new TableExistsMatcher<SELF>(getContainer(), catalogName, schemaName, tableName);
        return matcher;
    }

    /**
     * Precondition: drop table if it exists
     *
     * Drop a table if it exists. It is not an error if more than one
     * table matches the criteria.
     *
     * @param catalogName catalogName, may be null
     * @param schemaName schemaName, may be null
     * @param tableName tableName, may be null
     * @return Hamcrest matcher
     */
    default Matcher<Void> dropTableifExists(String catalogName, String schemaName, String tableName) {
        final Matcher<Void> matcher = new DropTableIfExistsMatcher<SELF>(
            getContainer(),
            catalogName,
            schemaName,
            tableName,
            false
        );
        return matcher;
    }

    /**
     * Precondition: drop table if it exists
     *
     * Drop a table if it exists. It is an error if more than one
     * table matches the criteria.
     *
     * @param catalogName catalogName, may be null
     * @param schemaName schemaName, may be null
     * @param tableName tableName, may be null
     * @return Hamcrest matcher
     */
    default Matcher<Void> strictDropTableifExists(String catalogName, String schemaName, String tableName) {
        final Matcher<Void> matcher = new DropTableIfExistsMatcher<SELF>(
            getContainer(),
            catalogName,
            schemaName,
            tableName,
            true
        );
        return matcher;
    }
}
