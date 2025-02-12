package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresDocker extends PostgresServer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    private static final JdbcDatabaseContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    public static final int MAX_CONNECTIONS = 199;
    public static final String DB_NAME = "postrise";
    private static final String DB_USER = "postrise";
    private static final String DB_PASS = "postrise";

    static {
        container
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USER)
                .withPassword(DB_PASS)
                .withCommand("postgres -c max_connections=" + MAX_CONNECTIONS);
    }

    PostgresDocker() {
        addListener(new PostriseDatabase());
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
        super.beforeCreate(settings);
        settings.setAutoCommit(false);
        settings.setAutoCommit(true);
        settings.isAutoCommit();
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }

    public static final void start() {
        container.start();
        LOGGER.info("{} container started.", container.getDockerImageName());
    }

    public static final void stop() {
        container.stop();
        LOGGER.info("{} container stopped.", container.getDockerImageName());
    }
}
