package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DefaultUserSecurity implements SecurityEventListener {

    private static final String SQL_CHECK_LOGIN_USER = String.join(" ",
            "SELECT",
            "pg_roles.rolsuper AS is_super_user,",
            "pg_roles.rolcanlogin AS is_login_user",
            "FROM pg_roles",
            "WHERE pg_roles.rolname = ? LIMIT 1");

    @Override
    public void onLogin(final Connection connection, final String user) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_CHECK_LOGIN_USER)) {
            stmt.setString(1, user);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("ERROR: user '" + user + "' does not exist");
                }
                if (!rs.getBoolean("is_login_user")) {
                    throw new SQLException("ERROR: '" + user + "' is not a login user");
                }
                if (rs.getBoolean("is_super_user")) {
                    throw new SQLException("ERROR: login user '" + user + "' is a super user");
                }
            }
        }
    }

    @Override
    public void onConnection(final Connection connection, final String role) throws SQLException {
        // TODO: Default is NO-OP for now, figure out a fast way to check. We may not
        // want to query the pg_roles table everytime a connection is requested.
    }
}
