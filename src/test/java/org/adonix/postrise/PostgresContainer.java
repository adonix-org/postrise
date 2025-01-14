package org.adonix.postrise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

abstract class PostgresContainer extends PostgresServer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    protected static final JdbcDatabaseContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    public static final void start() {
        container.start();
        LOGGER.info("{} container started.", container.getDockerImageName());
    }

    public static final void stop() {
        container.stop();
        LOGGER.info("{} container stopped.", container.getDockerImageName());
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(container.withDatabaseName(settings.getDatabaseName()).getJdbcUrl());
    }
}
