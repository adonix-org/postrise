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

    protected final JdbcDatabaseContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    public static final int MAX_CONNECTIONS = 199;
    public static final String DB_NAME = "postrise";
    public static final String DB_USER = "postrise";
    public static final String DB_PASS = "postrise";

    PostgresDocker() {
        container
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USER)
                .withPassword(DB_PASS)
                .withCommand("postgres -c max_connections=" + MAX_CONNECTIONS);

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
        settings.setAutoCommit(true);
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }

    public final void startContainer() {
        container.start();
        LOGGER.info("{} container started.", container.getDockerImageName());
    }

    public final void stopContainer() {
        container.stop();
        LOGGER.info("{} container stopped.", container.getDockerImageName());
    }
}
