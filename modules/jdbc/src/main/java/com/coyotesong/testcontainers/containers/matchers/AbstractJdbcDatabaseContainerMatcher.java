package com.coyotesong.testcontainers.containers.matchers;

import junit.framework.AssertionFailedError;
import org.hamcrest.TypeSafeMatcher;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Precondition: trop a table if it exists
 */
public abstract class AbstractJdbcDatabaseContainerMatcher<
    SELF extends org.testcontainers.containers.JdbcDatabaseContainer<SELF> & JdbcDatabaseContainerMatcher<SELF>
>
    extends TypeSafeMatcher<Void> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(
        AbstractJdbcDatabaseContainerMatcher.class
    );

    private final SELF container;

    private DatabaseMetaData metadata;

    /** List of tables found by 'listTables' */
    protected List<String> tables = new ArrayList<>();

    /**
     * Constructor
     *
     * @param container JdbcDatabaseContainer
     */
    protected AbstractJdbcDatabaseContainerMatcher(SELF container) {
        this.container = container;
    }

    /**
     * Get JDBC URL
     *
     * TODO: this should sanitize properties, esp. passwords and other sensitive content!
     *
     * @return JDBC URL
     */
    protected String getJdbcUrl() {
        return container.getJdbcUrl();
    }

    /**
     * Normalize table name as fully-qualfiied table name from components
     *
     * @param catalogName catalog name
     * @param schemaName schema name
     * @param tableName table name
     * @return fully-qualified table name
     */
    protected String normalizeTableName(String catalogName, String schemaName, String tableName) {
        // FIXME - get catalog separator from metadata!
        final StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(catalogName)) {
            sb.append(catalogName);
            sb.append(".");
        }
        if (StringUtils.isNotBlank(schemaName)) {
            sb.append(schemaName);
            sb.append(".");
        }
        sb.append(tableName);

        return sb.toString();
    }

    /**
     * Create new database connection
     *
     * @return new database connection
     * @throws SQLException an error occurred
     */
    protected Connection createConnection() throws SQLException {
        return createConnection("");
    }

    /**
     * Create new database connection
     *
     * @param query connection properties
     * @return new database connection
     * @throws SQLException an error occurred
     */
    protected Connection createConnection(String query) throws SQLException {
        final Connection conn = container.createConnection(query);
        this.metadata = conn.getMetaData();

        return conn;
    }

    /**
     * Strip quotes from component of table name
     * @param name component of table name
     * @return stripped component
     */
    protected String stripQuotes(String name) {
        // FIXME - use metadata!
        return name.matches("(\"|'|`).*(\"|'|`)") ? name.substring(1, name.length() - 1) : name;
    }

    /**
     * Drop tables.
     *
     * @param conn database connection
     * @param tables list of fully-qualified table names
     */
    protected void dropTables(Connection conn, List<String> tables) {
        if (!tables.isEmpty()) {
            try (Statement stmt = conn.createStatement()) {
                for (String fqn : tables) {
                    try {
                        // FIXME: add 'CASCADE' ?
                        stmt.execute(String.format("DROP TABLE IF EXISTS %s", fqn));
                    } catch (SQLException e) {
                        throw new AssertionFailedError(
                            String.format(
                                "Unable to drop table '%s' at '%s': %s",
                                fqn,
                                container.getJdbcUrl(),
                                e.getMessage()
                            )
                        );
                    }
                }
            } catch (SQLException e) {
                throw new AssertionFailedError(
                    String.format("Unable to create statement at '%s': %s", container.getJdbcUrl(), e.getMessage())
                );
            }
        }
    }

    /**
     * List matching tables
     *
     * @param catalogName catalog name. May be null
     * @param schemaName schema name or pattern. May be null.
     * @param tableName table name or pattern. Must not be null.
     * @throws SQLException an error occurred
     */
    protected void listTables(String catalogName, String schemaName, String tableName) throws SQLException {
        tables.clear();
        try (
            ResultSet rs = metadata.getTables(
                stripQuotes(catalogName),
                stripQuotes(schemaName),
                stripQuotes(tableName),
                null
            )
        ) {
            while (rs.next()) {
                tables.add(normalizeTableName(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            throw new AssertionFailedError(
                String.format(
                    "unable to extract tables matching '%s' at '%s': %s",
                    normalizeTableName(catalogName, schemaName, tableName),
                    container.getJdbcUrl(),
                    e.getMessage()
                )
            );
        }
    }
}
