package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    abstract DataSourceListener getDefaultRoleSecurity();

    private volatile DataSourceListener roleSecurity = getDefaultRoleSecurity();

    PostriseSecureDataSource(final String database) {
        super(database);
    }

    @Override
    public void onConnection(DataSourceContext context, Connection connection, String roleName) throws SQLException {
        roleSecurity.onConnection(context, connection, roleName);
    }

    @Override
    public void onLogin(DataSourceSettings settings, Connection connection) throws SQLException {
        roleSecurity.onLogin(settings, connection);
    }

    @Override
    public DataSourceListener getRoleSecurity() {
        return roleSecurity;
    }

    @Override
    public void setRoleSecurity(final DataSourceListener security) {
        this.roleSecurity = security;
    }
}
