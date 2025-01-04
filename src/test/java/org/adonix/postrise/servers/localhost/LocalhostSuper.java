package org.adonix.postrise.servers.localhost;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import org.adonix.postrise.PostgresServer;
import org.adonix.postrise.SuperUserAccess;
import org.adonix.postrise.security.SecurityEventListener;

public class LocalhostSuper extends PostgresServer {

    public LocalhostSuper() {
        addListener(new SuperUserAccess());
    }

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }
}
