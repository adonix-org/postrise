package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.POSTGRES_STRICT_SECURITY;

class DeltaServer extends PostgresTestServer {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setSecurity(POSTGRES_STRICT_SECURITY);
        settings.setLoginRole("delta_login");
        settings.setLoginPassword("helloworld");
    }
}
