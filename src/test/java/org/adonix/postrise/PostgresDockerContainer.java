package org.adonix.postrise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDockerContainer extends PostgreSQLContainer<PostgresDockerContainer> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    public PostgresDockerContainer() {
        super(POSTGRES_IMAGE_NAME);
    }

    @Override
    public void start() {
        LOGGER.info("Starting container {}...", getDockerImageName());
        super.start();
        LOGGER.info("Container {} started", getDockerImageName());
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping container {}...", getDockerImageName());
        super.stop();
        LOGGER.info("Container {} stopped", getDockerImageName());
    }
}
