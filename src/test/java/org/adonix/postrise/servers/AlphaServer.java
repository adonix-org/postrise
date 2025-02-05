package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.adonix.postrise.DataSourceContext;
import org.adonix.postrise.DataSourceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlphaServer extends PostgresTestServer {

    private static final Logger LOGGER = LogManager.getLogger(AlphaServer.class);

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }

    @Override
    public void afterCreate(final DataSourceContext context) {
        super.afterCreate(context);
        LOGGER.debug(
                "{}.afterCreate() Max Pool Size: {} Total Connections: {} Idle Connections: {} ActiveConnections: {}",
                context.getDatabaseName(),
                context.getMaxPoolSize(),
                context.getTotalConnections().get(),
                context.getIdleConnections().get(),
                context.getActiveConnections().get());
    }

    @Override
    public void afterClose(final DataSourceContext context) {
        
        runSafe(() -> getConnection(context.getDatabaseName(), "postrise"));
        super.afterClose(context);
    }

    @Override
    protected void beforeClose() {
        super.beforeClose();
        this.close();
        for (final String database : getDatabaseNames()) {
            final DataSourceContext context = getDataSource(database);
            LOGGER.debug(
                    "Database: {} Max Pool Size: {} Total Connections: {} Idle Connections: {} ActiveConnections: {}",
                    context.getDatabaseName(),
                    context.getMaxPoolSize(),
                    context.getTotalConnections().get(),
                    context.getIdleConnections().get(),
                    context.getActiveConnections().get());
        }
        runSafe(() -> getDataSource("postrise_again"));
    }

    @Override
    protected void afterClose() {
        LOGGER.debug("Databases Size: {}", getDatabaseNames().size());
        runSafe(() -> getDataSource("postrise"));
        super.afterClose();
    }
}
