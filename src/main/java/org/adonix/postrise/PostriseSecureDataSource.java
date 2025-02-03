package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.RoleSecurityListener;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract RoleSecurityListener getDefaultRoleSecurity();

    private RoleSecurityListener roleSecurity;

    PostriseSecureDataSource(final String database) {
        super(database);
        this.roleSecurity = getDefaultRoleSecurity();
    }

    @Override
    public void onConnection(final DataSourceContext context, final Connection connection, final String roleName)
            throws SQLException {
        roleSecurity.onConnection(context, connection, roleName);
    }

    @Override
    public void onLogin(final DataSourceSettings settings, final Connection connection) throws SQLException {
        roleSecurity.onLogin(settings, connection);
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
