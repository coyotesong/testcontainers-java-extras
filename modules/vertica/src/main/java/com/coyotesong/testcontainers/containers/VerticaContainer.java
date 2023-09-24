package com.coyotesong.testcontainers.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * TestContainer for Vertica CE databases
 *
 * See https://hub.docker.com/r/vertica/vertica-ce
 *
 * See https://docs.vertica.com/12.0.x/en/getting-started/introducing-vmart-example-db/
 *
 *     // https://hub.docker.com/r/vertica/vertica-ce
 *     // https://www.vertica.com/
 *     // https://www.microfocus.com/en-us/legal/software-licensing
 *
 * @param <SELF> this class
 */
public class VerticaContainer<SELF extends VerticaContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    private static final Logger LOG = LoggerFactory.getLogger(VerticaContainer.class);

    /** Short name of database container */
    public static final String NAME = "Vertica";

    /** Default image name */
    public static final String IMAGE = "vertica/vertica-ce";

    /** Default version */
    public static final String DEFAULT_TAG = "23.3.0-0";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);

    private static final Integer VERTICA_DATABASE_PORT = 5433;

    private static final Integer[] VERTICA_PORTS = { VERTICA_DATABASE_PORT, 5444 };

    static final String DEFAULT_DATABASE = "vmart";

    static final String DEFAULT_USER = "dbadmin"; // "newdbadmin" ?

    static final String DEFAULT_PASSWORD = "vertica";

    private String databaseName = DEFAULT_DATABASE;

    private String username = DEFAULT_USER;

    private String password = DEFAULT_PASSWORD;

    private Integer loginTimeout;

    private String keyStorePath;

    private String keyStorePassword;

    private String trustStorePath;

    private String trustStorePassword;

    /**
     * Default constructor
     */
    public VerticaContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Constructor taking image name
     *
     * @param dockerImageName image name
     */
    public VerticaContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        this.waitStrategy =
            new LogMessageWaitStrategy()
                .withRegEx(".*Vertica is now running.*\\s")
                .withStartupTimeout(Duration.ofMinutes(5));
        // this.setCommand("...");

        for (Integer port : VERTICA_PORTS) {
            this.addExposedPort(port);
        }
    }

    @Override
    protected void configure() {
        // no effect?...

        // addEnv("VERTICA_DB_USER", username);
        // if (StringUtils.isNotBlank(password)) {
        //     addEnv("VERTICA_DB_PASSWORD", password);
        // }

        // addEnv("APP_DB_USER", username);
        // if (StringUtils.isNotBlank(password)) {
        //     addEnv("APP_DB_PASSWORD", password);
        // }

        // addEnv("TZ", "Europe/Prague");

        urlParameters.put("user", username);
        if (StringUtils.isNotBlank(password)) {
            urlParameters.put("password", password);
        }

        if (loginTimeout != null) {
            urlParameters.put("loginTimeout", Integer.toString(loginTimeout));
        }

        if (StringUtils.isNotBlank(keyStorePath) && StringUtils.isNotBlank(keyStorePassword)) {
            urlParameters.put("KeyStorePath", keyStorePath);
            urlParameters.put("KeyStorePassword", keyStorePassword);
        }

        if (StringUtils.isNotBlank(trustStorePath) && StringUtils.isNotBlank(trustStorePassword)) {
            urlParameters.put("TrustStorePath", trustStorePath);
            urlParameters.put("TrustStorePassword", trustStorePassword);
        }
    }

    @Override
    public String getDriverClassName() {
        return "com.vertica.jdbc.Driver";
    }

    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return (
            "jdbc:vertica://" +
            getHost() +
            ":" +
            getMappedPort(VERTICA_DATABASE_PORT) +
            "/" +
            databaseName +
            additionalUrlParams
        );
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Get login timeout
     *
     * @return login timeout (in seconds?)
     */
    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * Get keyStore path
     *
     * @return keystore path, possibly null
     */
    // do not provide password?
    public String getKeystorePath() {
        return keyStorePath;
    }

    /**
     * Get trustStore path
     *
     * @return truststore path, possibly null
     */
    // do not provide password?
    public String getTrustStorePath() {
        return trustStorePath;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
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

    /**
     * Spocify login timeout
     *
     * @param loginTimeout timeout (in seconds?)
     * @return this object
     */
    public SELF withLoginTimeout(final Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
        return self();
    }

    /**
     * Specify keyStore path. Must be JKS file.
     *
     * TODO: verify file exists and is JKS file.
     *
     * @param keyStorePath path to keyStore file (JKS)
     * @return this object
     */
    public SELF withKeyStorePath(final String keyStorePath) {
        this.keyStorePath = keyStorePath;
        return self();
    }

    /**
     * Specify keyStore password
     *
     * @param keyStorePassword keyStore password
     * @return this object
     */
    public SELF withKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return self();
    }

    /**
     * Specify trustStore path. Must be JKS file.
     *
     * TODO: verify file exists and is JKS file.
     *
     * @param trustStorePath path to trustStore file (JKS)
     * @return this object
     */
    public SELF withTrustStorePath(final String trustStorePath) {
        this.trustStorePath = trustStorePath;
        return self();
    }

    /**
     * Specify trustStore password
     *
     * @param trustStorePassword truststore password
     * @return this object
     */
    public SELF withTrustStorePassword(final String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
