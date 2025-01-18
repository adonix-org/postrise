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
    public void close() {
        for (final String database : getDatabases()) {
            getStatus(database).ifPresent((status) -> {
                LOGGER.info("Max Pool Size: {}\tTotal Connections: {}\tIdle Connections: {}\tActiveConnections: {}",
                        status.getMaxPoolSize(), status.getTotalConnections(), status.getIdleConnections(),
                        status.getActiveConnections());
            });
        }
        super.close();
    }
}
