package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class PostgresStrictSecurity extends PostgresDefaultSecurity {

    protected PostgresStrictSecurity() {
        super();
    }

    @Override
    public void onConnection(Connection connection, String role) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ROLE_PRIVILEGES)) {
            stmt.setString(1, role);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SecurityException("role '" + role + "' does not exist");
                }
                if (rs.getBoolean("is_login_user")) {
                    throw new SecurityException("role '" + role + "' is a login user");
                }
                if (rs.getBoolean("is_super_user")) {
                    throw new SecurityException("role '" + role + "' is a super user");
                }
            }
        }
    }
}
