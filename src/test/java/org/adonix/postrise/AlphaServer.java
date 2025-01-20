package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AlphaServer extends PostgresTestServer {

    @Override
    public void onConnection(Connection connection, String roleName) throws SQLException {
        DISABLE_SECURITY.onConnection(connection, roleName);
    }

    @Override
    public void onLogin(Connection connection, String roleName) throws SQLException {
        DISABLE_SECURITY.onLogin(connection, roleName);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void afterCreate(final DataSourceContext context) {
        LOGGER.debug(
                "afterCreate: {}\t Max Pool Size: {}\tTotal Connections: {}\tIdle Connections: {}\tActiveConnections: {}",
                context.getDatabaseName(),
                context.getMaxPoolSize(),
                context.getTotalConnections(),
                context.getIdleConnections(),
                context.getActiveConnections());
    }

    @Override
    protected void beforeClose() {
        super.beforeClose();
        for (final String database : getDatabases()) {
            getDataSource(database).ifPresent((context) -> {
                LOGGER.debug(
                        "Database: {}\t Max Pool Size: {}\tTotal Connections: {}\tIdle Connections: {}\tActiveConnections: {}",
                        context.getDatabaseName(),
                        context.getMaxPoolSize(),
                        context.getTotalConnections(),
                        context.getIdleConnections(),
                        context.getActiveConnections());
            });
        }
    }

    @Override
    protected void afterClose() {
        LOGGER.debug("Databases Size: {}", getDatabases().size());
        super.afterClose();
    }
}
