package org.adonix.postrise;

import java.sql.Connection;

public final class PostgresDataSourceNoRoles extends PostgresDataSource {

    public PostgresDataSourceNoRoles(final Server server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public Connection getConnection(final String roleName) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support roles");
    }
}
