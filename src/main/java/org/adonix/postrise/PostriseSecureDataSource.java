package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.RoleSecurityEvent;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract RoleSecurityEvent getDefaultSecurity();

    private RoleSecurityEvent security = getDefaultSecurity();

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
    public RoleSecurityEvent getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(final RoleSecurityEvent security) {
        this.security = security;
    }
}
