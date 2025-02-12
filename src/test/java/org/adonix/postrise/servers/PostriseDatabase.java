package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.adonix.postrise.DataSourceContext;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;

class PostriseDatabase implements DatabaseListener {

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
        throw new RuntimeException("Not an error, just testing exception propogation.");
    }
}
