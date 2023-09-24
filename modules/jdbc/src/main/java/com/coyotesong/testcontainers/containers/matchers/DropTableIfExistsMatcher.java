package com.coyotesong.testcontainers.containers.matchers;

import junit.framework.AssertionFailedError;
import org.hamcrest.Description;
import org.hamcrest.internal.SelfDescribingValue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Precondition: trop a table if it exists
 */
public class DropTableIfExistsMatcher<
    SELF extends org.testcontainers.containers.JdbcDatabaseContainer<SELF> & JdbcDatabaseContainerMatcher<SELF>
>
    extends AbstractJdbcDatabaseContainerMatcher<SELF> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DropTableIfExistsMatcher.class);

    private final String catalogName;

    private final String schemaName;

    private final String tableName;

    private final boolean strict;

    /**
     * Constructor
     *
     * @param container JdbcDatabaseContainer
     * @param catalogName catalog name
     * @param schemaName schema name
     * @param tableName table name
     * @param strict if true then throw exception if more than one table matches
     */
    protected DropTableIfExistsMatcher(
        SELF container,
        String catalogName,
        String schemaName,
        String tableName,
        boolean strict
    ) {
        super(container);
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.strict = strict;
    }

    @Override
    public void describeMismatchSafely(Void v, Description description) {
        if (strict && (tables.size() > 1)) {
            description.appendText("Too many matches " + tables.size());
            description.appendList(
                " - ",
                "\n - ",
                "\n",
                tables.stream().map(SelfDescribingValue::new).collect(Collectors.toList())
            );
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Table " + tableName);
    }

    @Override
    public boolean matchesSafely(Void v) {
        boolean success = true;
        try (Connection conn = createConnection()) {
            listTables(catalogName, schemaName, tableName);
            if ((tables.size() == 1) || (tables.size() > 1 && !strict)) {
                dropTables(conn, tables);
            }
        } catch (SQLException e) {
            throw new AssertionFailedError(
                String.format("Unable to connect to database '%s': %s", getJdbcUrl(), e.getMessage())
            );
        }
        return success;
    }
}
