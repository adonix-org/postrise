package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.SecurityListener;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract SecurityListener getDefaultSecurity();

    private SecurityListener security = getDefaultSecurity();

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
    public SecurityListener getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(final SecurityListener security) {
        this.security = security;
    }
}
