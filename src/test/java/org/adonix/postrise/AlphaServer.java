package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import org.adonix.postrise.security.SecurityEventListener;

public class AlphaServer extends PostgresContainerServer {

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        super.onConfigure(settings);
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }
}
