package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;

public class BetaServer extends PostgresDocker {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setUsername("beta_login");
        settings.setPassword("helloworld");
    }
}
