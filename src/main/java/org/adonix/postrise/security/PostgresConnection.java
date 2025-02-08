package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConnection {

    /**
     * @param connection
     * @throws SQLException
     */
    public static final void reset(final Connection connection) throws SQLException {
        // TODO: Check if there are other attributes of the connection from the pool to
        // be reset.
        PostgresRoleDAO.resetRole(connection);
    }
}
