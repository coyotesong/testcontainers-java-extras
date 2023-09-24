package com.coyotesong.testcontainers.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * TestContainer for SAP HANA Express databases
 *
 * IMPORTANT - THE CURRENT CREDENTIALS ARE INCORRECT!
 *
 * See: https://hub.docker.com/r/saplabs/hanaexpress
 *
 * Requirements in /etc/sysctl.conf file:
 *   fs.file-max=20000000
 *   fs.aio-max-nr=262144
 *   vm.memory_failure_early_kill=1
 *   vm.max_map_count=135217728
 *   net.ipv4.ip_local_port_range=40000 60999
 *
 * Docker command line:
 *  sudo docker run -p 39013:39013 -p 39017:39017 -p 39041-39045:39041-39045 -p 1128-1129:1128-1129 -p 59013-59014:59013-59014 \
 *   -v /data/{{ }directory_name }}:/hana/mounts \
 *   --ulimit nofile=1048576:1048576 \
 *   --sysctl kernel.shmmax=1073741824 \
 *   --sysctl net.ipv4.ip_local_port_range='40000 60999' \
 *   --sysctl kernel.shmmni=524288 \
 *   --sysctl kernel.shmall=8388608 \
 *   --name {{ container_name }} \
 *   store/saplabs/hanaexpress:{{ tag }} \
 *   --passwords-url {{ url }} \
 *   --agree-to-sap-license
 *
 * List of connection properties: https://help.sap.com/docs/SAP_HANA_PLATFORM/0eec0d68141541d1b07893a39944924e/109397c2206a4ab2a5386d494f4cf75e.html
 * List of TCP/IP ports of all SAP Products: https://help.sap.com/docs/Security/575a9f0e56f34c6e8138439eefc32b16/616a3c0b1cc748238de9c0341b15c63c.html
 *
 * @param <SELF> this class
 */
public class SapHanaContainer<SELF extends SapHanaContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    private static final Logger LOG = LoggerFactory.getLogger(SapHanaContainer.class);

    /** Short name of the database container */
    public static final String NAME = "SAP HANA";

    /** Default imnage name */
    public static final String IMAGE = "saplabs/hanaexpress";

    /** Default tag */
    public static final String DEFAULT_TAG = "2.00.061.00.20220519.1";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);

    // this is specific to the docker image
    private static final int INSTANCE_ID = 90;

    private static final int TENANT_ID = 1;

    // indexserver
    private static final Integer BASE_SAP_HANA_INDEX_SERVER_DATABASE_PORT = 30013;

    // statisticsserver or index server
    private static final Integer BASE_SAP_HANA_SYSTEM_DATABASE_PORT = 30017;

    // indexserver, scriptserver, docstore, xsengine, diserver, indexservers?
    private static final Integer BASE_SAP_HANA_TENANT_DATABASE_PORT = 30040;

    // SAP Host Agent (SAP/HTTP) (service: /etc/saphostctrl)
    private static final Integer BASE_SAP_HANA_HOST_AGENT_HTTP_PORT = 1128;

    // SAP Host Agent (SAP/HTTPS) (service: /etc/saphostctrls)
    private static final Integer BASE_SAP_HANA_HOST_AGENT_HTTPS_PORT = 1129;

    // SAP Web Dispatcher
    private static final Integer BASE_SAP_HANA_PLATFORM_PORT = 8000;

    // Instance agent (SOAP/HTTP)
    private static final Integer BASE_SAP_HANA_SAP_CTRL_PORT = 50013;

    // ports are for systemdb and first tenant.
    private static final Integer[] SAP_HANA_PORTS = {
        getIndexServerDatabasePort(INSTANCE_ID),
        getSystemDatabasePort(INSTANCE_ID),
        getTenantDatabasePort(INSTANCE_ID, TENANT_ID),
        getHostAgentHttpPort(),
        getHostAgentHttpsPort(),
        getHanaPlatformPort(INSTANCE_ID),
        getSapCtrlPort(INSTANCE_ID),
    };

    // SYSTEM/manager ?
    static final String DEFAULT_TENANT_DATABASE = "HXE";

    static final String DEFAULT_USER = "hxeadm";

    static final String DEFAULT_PASSWORD = "HXEHana1";

    private final String databaseName = DEFAULT_TENANT_DATABASE;

    private final String username = DEFAULT_USER;

    private final String password = DEFAULT_PASSWORD;

    private final Integer instanceId;

    private final Integer tenantId;

    /**
     * Get index server database port
     *
     * @param instanceId instance id (1 to 99 inclusive)
     * @return index server database port
     */
    private static final Integer getIndexServerDatabasePort(int instanceId) {
        return BASE_SAP_HANA_INDEX_SERVER_DATABASE_PORT + 100 * instanceId;
    }

    /**

    /**
     * Get system database port
     *
     * @param instanceId instance id (1 to 99 inclusive)
     * @return system database port
     */
    private static final Integer getSystemDatabasePort(int instanceId) {
        return BASE_SAP_HANA_SYSTEM_DATABASE_PORT + 100 * instanceId;
    }

    /**
     * Get tenant database port
     *
     * @param instanceId instance id (1 to 99 inclusive)
     * @param tenantId tenant id (1 to 99 inclusinve)
     * @return tenant database port
     */
    private static final Integer getTenantDatabasePort(int instanceId, int tenantId) {
        return BASE_SAP_HANA_TENANT_DATABASE_PORT + 100 * instanceId + tenantId;
    }

    /**
     * Get SAP Host Agent with SOAP/HTTP
     *
     * @return host agent (http)
     */
    private static final Integer getHostAgentHttpPort() {
        return BASE_SAP_HANA_HOST_AGENT_HTTP_PORT;
    }

    /**
     * Get SAP Host Agent with SOAP/HTTPS
     *
     * @return host agent (https)
     */
    private static final Integer getHostAgentHttpsPort() {
        return BASE_SAP_HANA_HOST_AGENT_HTTPS_PORT;
    }

    /**
     * Get SAP HANA platform port
     *
     * @param instanceId
     * @return SAP HANA platform port
     */
    private static final Integer getHanaPlatformPort(int instanceId) {
        return BASE_SAP_HANA_PLATFORM_PORT + instanceId;
    }

    /**
     * Gete SAP CTRL port
     *
     * @param instanceId
     * @return SAP ctrl port
     */
    private static final Integer getSapCtrlPort(int instanceId) {
        return BASE_SAP_HANA_SAP_CTRL_PORT + 100 * instanceId;
    }

    /**
     * Default constructor
     */
    public SapHanaContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Constructor taking explicit image name.
     *
     * @param dockerImageName image name
     */
    public SapHanaContainer(final DockerImageName dockerImageName) {
        this(dockerImageName, INSTANCE_ID, TENANT_ID);
    }

    /**
     * Constructor taking explicit image name, instance id, and tenant id
     *
     * @param dockerImageName image name
     * @param instanceId instance id, from 1 to 99 inclusive
     * @param tenantId tenant id, from 1 to 5(?) inclusive
     */
    protected SapHanaContainer(final DockerImageName dockerImageName, Integer instanceId, Integer tenantId) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        this.instanceId = instanceId;
        this.tenantId = tenantId;

        // super.withLimitNoFile(1048576, 1048576);
        // super.withIpLocalPortRange(40000, 60999);
        // super.withSharedMemoryMax(1_073_741_824); // kernel.shmmax
        // super.withSharedMemoryNI(524_288);   // kernel.shmmni
        // super.withSharedMemoryAll(8_388_608); // kernel.shmall
        super.withSharedMemorySize(8_388_608L);

        final String passwordUrl = "/hana/mounts/password.json";

        // must set permission to 600, ownership to 12000:79
        this.withCopyToContainer(Transferable.of("{ \"master_password\": \"" + password + "\" }"), passwordUrl);

        this.waitStrategy =
            new LogMessageWaitStrategy()
                .withRegEx(".*Startup finished!.*\\s")
                .withStartupTimeout(Duration.ofMinutes(5));
        // LOG.info("command parts: {}", String.join(",", this.getCommandParts()));
        this.setCommand("--passwords-url file://" + passwordUrl + " --agree-to-sap-license");

        // FIXME: the tenant database port may not match the tenant ID!

        this.addExposedPort(getIndexServerDatabasePort(instanceId));
        this.addExposedPort(getSystemDatabasePort(instanceId));
        this.addExposedPort(getTenantDatabasePort(instanceId, 1));
        this.addExposedPort(getTenantDatabasePort(instanceId, 2));
        this.addExposedPort(getTenantDatabasePort(instanceId, 3));
        this.addExposedPort(getTenantDatabasePort(instanceId, 4));
        this.addExposedPort(getTenantDatabasePort(instanceId, 5));
        this.addExposedPort(getHanaPlatformPort(instanceId));
        this.addExposedPort(getSapCtrlPort(instanceId));
        this.addExposedPort(getHostAgentHttpPort());
        this.addExposedPort(getHostAgentHttpsPort());
    }

    /**
     * Get mapped port for index server database
     * @return current index server database port
     */
    public Integer getMappedIndexServerDatabasePort() {
        return getMappedPort(getIndexServerDatabasePort(getInstanceId()));
    }

    /**
     * Get mapped port for system database
     * @return current system database port
     */
    public Integer getMappedSystemDatabasePort() {
        return getMappedPort(getSystemDatabasePort(getInstanceId()));
    }

    /**
     * Get mapped port for tenant database
     * @return current tenant database port
     */
    public Integer getMappedTenantDatabasePort() {
        return getMappedPort(getTenantDatabasePort(getInstanceId(), getTenantId()));
    }

    /**
     * Get mapped port for HANA platform port
     * @return current HANA platformservice port
     */
    public Integer getMappedHanaPlatformPort() {
        return getMappedPort(getHanaPlatformPort(getInstanceId()));
    }

    /**
     * Get mapped port for SAP Ctrl service
     * @return current SAP Ctrl service port
     */
    public Integer getMappedSapCtrlDatabasePort() {
        return getMappedPort(getSapCtrlPort(getInstanceId()));
    }

    @Override
    protected void configure() {
        // this is just a few of the options
        // see https://help.sap.com/docs/SAP_HANA_PLATFORM/0eec0d68141541d1b07893a39944924e/109397c2206a4ab2a5386d494f4cf75e.html
        if (StringUtils.isNotBlank(databaseName)) {
            urlParameters.put("databaseName", databaseName);
        }

        urlParameters.put("user", username);
        // urlParameters.put("user", "SYSTEM");
        if (StringUtils.isNotBlank(password)) {
            urlParameters.put("password", password);
        }
    }

    @Override
    public String getDriverClassName() {
        return "com.sap.db.jdbc.Driver";
    }

    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return "jdbc:sap://" + getHost() + ":" + getMappedTenantDatabasePort() + "/" + additionalUrlParams;
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
        return "SELECT 1 FROM dummy";
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Get SAP HANA instance id
     *
     * Note: this will always be 70 with the default docker image.
     *
     * @return number from 1 to 99, inclusive
     */
    public int getInstanceId() {
        return instanceId;
    }

    /**
     * Get SAP HANA tenant id
     *
     * Note: this will always be 1 with the default docker image.
     *
     * @return number from 1 to 99, inclusive
     */
    public int getTenantId() {
        return tenantId;
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
