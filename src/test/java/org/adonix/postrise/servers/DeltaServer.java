package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_STRICT_ROLE_SECURITY;

import org.adonix.postrise.DataSourceSettings;

public class DeltaServer extends PostgresTestServer {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setRoleSecurity(POSTGRES_STRICT_ROLE_SECURITY);
        settings.setLoginRole("delta_login");
        settings.setLoginPassword("helloworld");
    }
}
