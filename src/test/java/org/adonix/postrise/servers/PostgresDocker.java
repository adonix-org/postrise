package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceContext;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresDocker extends PostgresServer {

    private static final Logger LOGGER = LogManager.getLogger(PostgresDocker.class);

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

    protected PostgresDocker() {
        LOGGER.debug("Calling constructor for {}", this);
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
        settings.setLoginRole(container.getUsername());
        settings.setLoginPassword(container.getPassword());
    }

    @Override
    public void afterCreate(final DataSourceContext context) {
        super.afterCreate(context);
        LOGGER.info(context.getDataSourceProperties());
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
