package org.adonix.postrise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.MySQLContainer;

public class MySQLDockerContainer extends MySQLContainer<MySQLDockerContainer> {

    private static final Logger LOGGER = LogManager.getLogger();

    public MySQLDockerContainer() {
        super("mysql:9.1.0");
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
