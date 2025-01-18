package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import org.adonix.postrise.security.SecurityEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AlphaServer extends PostgresTestServer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }

    @Override
    public void beforeClose() {
        super.beforeClose();
        for (final String database : getDatabases()) {
            getConnectionSettings(database).ifPresent((settings) -> {
                LOGGER.debug(
                        "Database: {}\t Max Pool Size: {}\tTotal Connections: {}\tIdle Connections: {}\tActiveConnections: {}",
                        settings.getDatabaseName(),
                        settings.getMaxPoolSize(),
                        settings.getTotalConnections(),
                        settings.getIdleConnections(),
                        settings.getActiveConnections());
            });
        }
    }

    @Override
    public void afterClose() {
        LOGGER.debug("Databases Size: {}", getDatabases().size());
        super.afterClose();
    }
}
