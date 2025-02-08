package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;

class PostriseDatabase implements DatabaseListener {

    @Override
    public String getDatabaseName() {
        return "postrise";
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setMaxPoolSize(1);
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }
}
