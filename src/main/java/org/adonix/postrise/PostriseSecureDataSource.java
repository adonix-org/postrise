package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.RoleSecurityListener;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract RoleSecurityListener getDefaultRoleSecurity();

    private RoleSecurityListener roleSecurity = getDefaultRoleSecurity();

    PostriseSecureDataSource(final String database) {
        super(database);
    }

    @Override
    public void onConnection(final Connection connection, final String roleName) throws SQLException {
        roleSecurity.onConnection(connection, roleName);
    }

    @Override
    public void onLogin(final Connection connection, final String roleName) throws SQLException {
        roleSecurity.onLogin(connection, roleName);
    }

    @Override
    public RoleSecurityListener getRoleSecurity() {
        return roleSecurity;
    }

    @Override
    public void setRoleSecurity(final RoleSecurityListener security) {
        this.roleSecurity = security;
    }
}
