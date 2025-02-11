package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;
import org.adonix.postrise.DataSourceSettings;

public class TestServer extends PostgresDocker {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }
}
