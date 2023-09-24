package com.coyotesong.testcontainers.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;

/**
 * TestContainer for SQLite embedded databases
 * <p>
 * Important: SQLite is not a traditional client/server database - it is a library that provides
 * a fairly robust SQL API via an external library. Since this class uses a bit of a
 * hack (it creates a simple docker image but does not use it) none of the advanced TestContainer
 * management will work with this Container.
 * <p>
 * I've developed this Container since SQLite is extremely popular with docker-based web applications
 * (e.g., Datasette). These applications often require local extensions to populate and update
 * the database and this Container should simplify development.
 * <p>
 * SQLite Pros:
 * <ul>
 *   <li>It is widely used and has a rich ecosystem</li>
 *   <li>It is fairly complete - triggers, user-defined functions and types, etc.</li>
 *   <li>It can use a memory buffer, a local file, or a memory buffer with a local spill file</li>
 * </ul>
 * <p>
 * SQLite Cons:
 * <ul>
 *   <li>It requires external system packages</li>
 *   <li>It has few native datatypes</li>
 *   <li>It does not support stored procedures (but can use UDF instead)</li>
 *   <li>It does not have the same performance and reliability of traditional databases</li>
 * </ul>
 * <p>
 * Debian packages:
 * <p>
 * - sqlite3
 * - sqlite3-tools
 * - sqlite3-doc
 * <p>
 * JDBC driver: org.xerial:sqlite-jdbc:3.42.0.0
 *
 * @param <SELF> this class
 */
public class SQLiteContainer<SELF extends SQLiteContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    private static final Logger LOG = LoggerFactory.getLogger(SQLiteContainer.class);

    /** Short name of the database container */
    public static final String NAME = "SQLite";

    /** Default image */
    public static final String IMAGE = "alpine";

    /** Default version */
    public static final String DEFAULT_TAG = "latest";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);

    // FIXME - use property java.io_tmpdir ?
    static final String DEFAULT_DATABASE = "/tmp/test.db";

    static final String DEFAULT_USER = "test";

    static final String DEFAULT_PASSWORD = "test";

    private boolean inMemoryDatabase = true;

    private String filename = DEFAULT_DATABASE;

    private String username = DEFAULT_USER;

    private String password = DEFAULT_PASSWORD;

    /**
     * Default constructor
     */
    public SQLiteContainer() {
        super(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
        this.waitStrategy = new LogMessageWaitStrategy().withStartupTimeout(Duration.ofMinutes(2));
    }

    /**
     * Default constructor taking an image name
     *
     * @param imageName image name
     */
    public SQLiteContainer(final DockerImageName imageName) {
        this();
    }

    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    public String getJdbcUrl() {
        if (inMemoryDatabase) {
            return "jdbc:sqlite::memory:";
        }
        return "jdbc:sqlite:" + getDatabaseName();
    }

    @Override
    public Connection createConnection(String queryString, Properties info)
        throws SQLException, NoDriverFoundException {
        final Driver d = getJdbcDriverInstance();
        final String url = getJdbcUrl();

        return d.connect(url, info);
    }

    @Override
    protected String constructUrlForConnection(String queryString) {
        return getJdbcUrl();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    /**
     * Is this an im-memory database?
     *
     * @return 'true' if an in-memory database
     */
    public boolean isInMemoryDatabase() {
        return inMemoryDatabase;
    }

    @Override
    public String getDatabaseName() {
        if (isInMemoryDatabase()) {
            return "";
        }
        return filename;
    }

    /**
     * Use an in-memory database
     *
     * @return this object
     */
    public SELF withInMemoryDatabase() {
        this.inMemoryDatabase = true;
        return self();
    }

    @Override
    public SELF withDatabaseName(final String filename) {
        this.inMemoryDatabase = false;
        this.filename = filename;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
