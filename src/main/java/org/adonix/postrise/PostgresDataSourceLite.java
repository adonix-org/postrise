package org.adonix.postrise;

import java.sql.Connection;

public final class PostgresDataSourceLite extends PostgresDataSource {

    public PostgresDataSourceLite(final Server server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public Connection getConnection(final String roleName) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support roles");
    }
}
