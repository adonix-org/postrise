package org.adonix.postrise.servers.localhost;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import org.adonix.postrise.PostgresTestServer;
import org.adonix.postrise.PostriseServer;
import org.adonix.postrise.security.SecurityEventListener;

public class LocalhostSuper extends PostriseServer {

    public LocalhostSuper() {
        addListener(new PostgresTestServer());
    }

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }
}
