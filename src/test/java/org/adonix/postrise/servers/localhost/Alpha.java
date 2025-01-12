package org.adonix.postrise.servers.localhost;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.PostgresServer;

public class Alpha extends PostgresServer {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setUsername("postrise_user");
    }
}
