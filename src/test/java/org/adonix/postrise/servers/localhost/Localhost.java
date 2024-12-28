package org.adonix.postrise.servers.localhost;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.PostriseServer;

public class Localhost extends PostriseServer {

    @Override
    public void onCreate(final ConnectionSettings settings) {
        settings.setUsername("adonix_user");
    }
}
