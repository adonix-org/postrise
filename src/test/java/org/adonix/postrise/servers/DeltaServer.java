package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;

public class DeltaServer extends PostgresDocker {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setUsername("delta_login");
        settings.setLoginPassword("helloworld");
    }
}
