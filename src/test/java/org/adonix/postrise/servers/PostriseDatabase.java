package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.adonix.postrise.DataSourceContext;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class PostriseDatabase implements DatabaseListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String getDatabaseName() {
        return "postrise";
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }

    @Override
    public void beforeClose(final DataSourceContext context) throws Exception {
        LOGGER.info("{}: Threads Awaiting Connection: {}", context, context.getThreadsAwaitingConnection());
        throw new RuntimeException("Not an error, just testing exception propogation.");
    }
}
