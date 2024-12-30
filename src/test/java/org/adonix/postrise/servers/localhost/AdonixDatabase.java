package org.adonix.postrise.servers.localhost;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.DatabaseListener;

class AdonixDatabase implements DatabaseListener {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setUsername("adonix_user");
    }

    @Override
    public String getDatabaseName() {
        return "adonix";
    }

}
