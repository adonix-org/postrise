package org.adonix.postrise;

import static org.adonix.postrise.PostgresDataSource.DEFAULT_HOST;
import static org.adonix.postrise.PostgresDataSource.DEFAULT_PORT;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresServer extends PostriseServer {

    @Override
    public String getHost() {
        return DEFAULT_HOST;
    }

    @Override
    public Integer getPort() {
        return DEFAULT_PORT;
    }

    @Override
    protected ConnectionProvider getConnectionProvider(final String database) {
        return new PostgresDataSource(this, database);
    }

    @Override
    protected void setRole(final Connection connection, final String role) throws SQLException {
        RoleDAO.setRole(connection, role);
    }
}
