package org.adonix.postrise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

abstract class PostgresTestServer extends PostgresServer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    private static final JdbcDatabaseContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    private static final String DB_NAME = "postrise";
    private static final String DB_USER = "postrise";
    private static final String DB_PASS = "postrise";

    static {
        container
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USER)
                .withPassword(DB_PASS);
    }

    protected PostgresTestServer() {
        LOGGER.debug("Calling constructor for {}", this.getClass().getSimpleName());
    }

    @Override
    public String getHostName() {
        return container.getHost();
    }

    @Override
    public Integer getPort() {
        return container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setLoginRole(container.getUsername());
        settings.setLoginPassword(container.getPassword());
    }

    public static final void start() {
        container.start();
        LOGGER.debug("{} container started.", container.getDockerImageName());
    }

    public static final void stop() {
        container.stop();
        LOGGER.debug("{} container stopped.", container.getDockerImageName());
    }
}
