package org.adonix.postrise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RoleDAO {

    private static final String SQL_SET_ROLE = "SELECT set_config('ROLE', ?, false)";

    public static final void setRole(final Connection connection, final String role) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SET_ROLE)) {
            stmt.setString(1, role);
            stmt.execute();
        }
    }
}
