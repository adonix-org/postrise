package org.adonix.postrise.servers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestServer extends PostgresDocker {

    private static final Logger LOGGER = LogManager.getLogger();

    public TestServer() {
        // Add listener a second time to generate warning.
        addListener(new PostriseDatabase());
    }

    @Override
    protected void beforeClose() {
        super.beforeClose();
        logStatus();
    }

    @Override
    protected void afterClose() {
        super.afterClose();
        logStatus();
    }

    private void logStatus() {
        LOGGER.info("{}: Connection Total: {} Active: {} Idle: {}", this,
                getTotalConnections(),
                getActiveConnections(),
                getIdleConnections());
    }
}
