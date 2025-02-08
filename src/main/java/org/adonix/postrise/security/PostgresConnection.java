package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConnection {

    /**
     * @param connection
     * @throws SQLException
     * @see https://github.com/brettwooldridge/HikariCP/wiki/Pool-Analysis
     */
    public static final void reset(final Connection connection) throws SQLException {
        PostgresRoleDAO.resetRole(connection);
    }
}
