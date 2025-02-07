package org.adonix.postrise.servers;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.security.PostgresRoleDAO;

public class SetRoleServer extends PostgresDocker {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setUsername("beta_login");
        settings.setLoginPassword("helloworld");
    }

    public Connection getConnection(final String databaseName, final String roleName) throws SQLException {
        final Connection connection = super.getConnection(databaseName);
        // TODO; Check role...
        PostgresRoleDAO.setRole(connection, roleName);
        return connection;
    }
}
