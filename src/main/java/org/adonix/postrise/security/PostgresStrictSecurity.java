package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresStrictSecurity extends PostgresDefaultSecurity {

    @Override
    public void onConnection(Connection connection, String role) throws SQLException {
        // TODO Implement checks on the role, mostly for development.
        throw new UnsupportedOperationException("Unimplemented method 'onConnection'");
    }
}
