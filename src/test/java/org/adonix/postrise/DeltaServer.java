package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.POSTGRES_STRICT_SECURITY;

import org.adonix.postrise.security.SecurityEventListener;

public class DeltaServer extends PostgresTestServer {

    private static final Server instance = new DeltaServer();

    private DeltaServer() {
    }

    /**
     * @return the singleton {@link GammaServer}.
     */
    static final Server getInstance() {
        return instance;
    }

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return POSTGRES_STRICT_SECURITY;
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setUsername("delta_login");
        settings.setPassword("helloworld");
    }
}
