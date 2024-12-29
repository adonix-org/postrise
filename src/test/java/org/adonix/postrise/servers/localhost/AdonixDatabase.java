package org.adonix.postrise.servers.localhost;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.DatabaseConnectionListener;

class AdonixDatabase implements DatabaseConnectionListener {

    @Override
    public void onCreate(ConnectionSettings settings) {
        settings.setUsername("adonix_user");
    }

    @Override
    public String getDatabaseName() {
        return "adonix";
    }

}
