package org.adonix.postrise;

import org.adonix.postrise.security.SecurityProviders;

public class PostriseDatabase implements DatabaseEvent {

    @Override
    public String getDatabaseName() {
        return "postrise";
    }

    @Override
    public void beforeCreate(DataSourceSettings settings) {
        settings.setSecurity(SecurityProviders.POSTGRES_STRICT_SECURITY);
    }
}
