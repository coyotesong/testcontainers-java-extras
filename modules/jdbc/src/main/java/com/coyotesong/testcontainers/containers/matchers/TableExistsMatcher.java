package com.coyotesong.testcontainers.containers.matchers;

import junit.framework.AssertionFailedError;
import org.hamcrest.Description;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Hamcrest matcher for database tables.
 */
public class TableExistsMatcher<
    SELF extends org.testcontainers.containers.JdbcDatabaseContainer<SELF> & JdbcDatabaseContainerMatcher<SELF>
>
    extends AbstractJdbcDatabaseContainerMatcher<SELF> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TableExistsMatcher.class);

    private final String catalogName;

    private final String schemaName;

    private final String tableName;

    /**
     * Constructor
     *
     * @param container JdbcDatabaseContainer object
     * @param catalogName catalog name
     * @param schemaName schema name
     * @param tableName table name
     */
    public TableExistsMatcher(SELF container, String catalogName, String schemaName, String tableName) {
        super(container);
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    @Override
    public void describeMismatchSafely(Void v, Description description) {
        if (!matches(tableName)) {
            description.appendText(
                String.format(
                    "No tables match '%s' at '%s'",
                    normalizeTableName(catalogName, schemaName, tableName),
                    getJdbcUrl()
                )
            );
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Table " + normalizeTableName(catalogName, schemaName, tableName));
    }

    @Override
    public boolean matchesSafely(Void v) {
        boolean found = false;
        try (Connection conn = createConnection()) {
            listTables(catalogName, schemaName, tableName);
        } catch (SQLException e) {
            throw new AssertionFailedError(
                String.format("Unable to connect to database '%s': %s", getJdbcUrl(), e.getMessage())
            );
        }
        return found;
    }
}
