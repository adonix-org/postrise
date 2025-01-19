package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.POSTGRES_STRICT_SECURITY;

import org.adonix.postrise.security.SecurityEventListener;

class DeltaServer extends PostgresTestServer {

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return POSTGRES_STRICT_SECURITY;
    }

    @Override
    public void onCreate(final ConnectionConfiguration config) {
        config.setLoginRole("delta_login");
        config.setLoginPassword("helloworld");
    }
}
