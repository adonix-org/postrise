package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresDataSourceNoRoles extends PostgresDataSource {

    protected PostgresDataSourceNoRoles(final Server server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public final Connection getConnection(final String roleName) throws SQLException {
        throw new UnsupportedOperationException("This data source does not allow roles");
    }
}
