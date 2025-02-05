package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;
import org.adonix.postrise.security.RoleSecurityProviders;

public class PostriseDatabase implements DatabaseListener {

    @Override
    public String getDatabaseName() {
        return "postrise";
    }

    @Override
    public void beforeCreate(DataSourceSettings settings) {
        settings.setRoleSecurity(RoleSecurityProviders.POSTGRES_STRICT_ROLE_SECURITY);
    }
}
