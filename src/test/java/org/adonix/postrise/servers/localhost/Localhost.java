package org.adonix.postrise.servers.localhost;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.PostriseServer;

public class Localhost extends PostriseServer {

    public Localhost() {
        addListener(new AdonixDatabase());
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
    }
}
