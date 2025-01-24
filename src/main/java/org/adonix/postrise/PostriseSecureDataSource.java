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
    public void onConnection(DataSourceContext context, Connection connection, String roleName) throws SQLException {
        roleSecurity.onConnection(context, connection, roleName);
    }

    @Override
    public void onLogin(final DataSourceSettings settings, final Connection connection) throws SQLException {
        roleSecurity.onLogin(settings, connection);
    }

    @Override
    public synchronized RoleSecurityListener getRoleSecurity() {
        return roleSecurity;
    }

    @Override
    public synchronized void setRoleSecurity(final RoleSecurityListener security) {
        this.roleSecurity = security;
    }
}
