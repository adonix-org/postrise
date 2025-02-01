package org.adonix.postrise;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AlphaServer extends PostgresTestServer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void beforeCreate(DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }

    @Override
    public void afterCreate(final DataSourceContext context) {
        super.afterCreate(context);
        LOGGER.debug(
                "[{}.afterCreate()] Max Pool Size: {} Total Connections: {} Idle Connections: {} ActiveConnections: {}",
                context.getDatabaseName(),
                context.getMaxPoolSize(),
                context.getTotalConnections(),
                context.getIdleConnections(),
                context.getActiveConnections());
    }

    @Override
    protected void beforeClose() {
        super.beforeClose();
        for (final String database : getDatabaseNames()) {
            final DataSourceContext context = getDataSource(database);
            LOGGER.debug(
                    "Database: {} Max Pool Size: {} Total Connections: {} Idle Connections: {} ActiveConnections: {}",
                    context.getDatabaseName(),
                    context.getMaxPoolSize(),
                    context.getTotalConnections(),
                    context.getIdleConnections(),
                    context.getActiveConnections());
            ;
        }
    }

    @Override
    protected void afterClose() {
        LOGGER.debug("Databases Size: {}", getDatabaseNames().size());
        super.afterClose();
    }
}
