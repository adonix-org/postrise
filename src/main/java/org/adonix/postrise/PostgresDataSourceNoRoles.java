package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

public final class PostgresDataSourceNoRoles extends PostgresDataSource {

    public PostgresDataSourceNoRoles(final Server server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public Connection getConnection(final String roleName) throws SQLException {
        throw new UnsupportedOperationException("This data source does not permit roles");
    }
}
