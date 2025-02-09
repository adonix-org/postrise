package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;

public class BetaDatabase implements DatabaseListener {

    @Override
    public String getDatabaseName() {
        return "database_beta";
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setMinIdle(20);
        settings.setUsername("beta_login");
        settings.setPassword("helloworld");
    }
}
