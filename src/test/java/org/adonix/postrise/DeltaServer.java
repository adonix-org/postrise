package org.adonix.postrise;

import static org.adonix.postrise.security.SecurityProviders.POSTGRES_STRICT_SECURITY;

import java.sql.Connection;
import java.sql.SQLException;

class DeltaServer extends PostgresTestServer {

    @Override
    public void onConnection(Connection connection, String roleName) throws SQLException {
        POSTGRES_STRICT_SECURITY.onConnection(connection, roleName);
    }

    @Override
    public void onLogin(Connection connection, String roleName) throws SQLException {
        POSTGRES_STRICT_SECURITY.onLogin(connection, roleName);
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setLoginRole("delta_login");
        settings.setLoginPassword("helloworld");
    }
}
