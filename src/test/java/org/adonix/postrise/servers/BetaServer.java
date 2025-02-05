package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;

public class BetaServer extends PostgresTestServer {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setLoginRole("beta_login");
        settings.setLoginPassword("helloworld");
    }
}
