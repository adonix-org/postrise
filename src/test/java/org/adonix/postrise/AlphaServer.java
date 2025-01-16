package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import org.adonix.postrise.security.SecurityEventListener;

class AlphaServer extends PostgresTestServer {

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }
}
