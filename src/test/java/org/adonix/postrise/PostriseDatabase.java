package org.adonix.postrise;

import org.adonix.postrise.security.RoleSecurityProviders;

public class PostriseDatabase implements DatabaseListener {

    @Override
    public String getDatabaseName() {
        return "postrise";
    }

    @Override
    public void beforeCreate(DataSourceSettings settings) {
        settings.setSecurity(RoleSecurityProviders.POSTGRES_STRICT_SECURITY);
    }
}
