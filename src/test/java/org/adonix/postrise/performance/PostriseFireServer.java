package org.adonix.postrise.performance;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.servers.PostgresDocker;

class PostriseFireServer extends PostgresDocker {

    private static final PostriseFireServer instance = new PostriseFireServer();

    private PostriseFireServer() {
    }

    static PostriseFireServer getInstance() {
        return instance;
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }
}
