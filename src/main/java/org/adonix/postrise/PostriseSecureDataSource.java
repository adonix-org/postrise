package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.SecurityProvider;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract SecurityProvider getDefaultSecurity();

    private SecurityProvider security = getDefaultSecurity();

    PostriseSecureDataSource(final String database) {
        super(database);
    }

    @Override
    public void onConnection(Connection connection, String roleName) throws SQLException {
        security.onConnection(connection, roleName);
    }

    @Override
    public void onLogin(Connection connection, String roleName) throws SQLException {
        security.onLogin(connection, roleName);
    }

    @Override
    public SecurityProvider getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(final SecurityProvider security) {
        this.security = security;
    }
}
