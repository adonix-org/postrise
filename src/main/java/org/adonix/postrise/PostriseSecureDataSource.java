package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.RoleSecurityListener;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract RoleSecurityListener getDefaultSecurity();

    private RoleSecurityListener security = getDefaultSecurity();

    PostriseSecureDataSource(final String database) {
        super(database);
    }

    @Override
    public void onConnection(final Connection connection, final String roleName) throws SQLException {
        security.onConnection(connection, roleName);
    }

    @Override
    public void onLogin(final Connection connection, final String roleName) throws SQLException {
        security.onLogin(connection, roleName);
    }

    @Override
    public RoleSecurityListener getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(final RoleSecurityListener security) {
        this.security = security;
    }
}
