package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import org.adonix.postrise.security.SecurityEventListener;

public class AlphaServer extends PostgresContainer {

    private static final Server instance = new AlphaServer();

    private AlphaServer() {
    }

    /**
     * @return the singleton{@link AlphaServer}.
     */
    public static final Server getInstance() {
        return instance;
    }

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }
}
