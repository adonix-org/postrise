package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresRoleDAO {

    /**
     * PostgreSQL specific query to SELECT privileges for a role from the
     * pg_roles TABLE.
     */
    private static final String SQL_SELECT_ROLE_PRIVILEGES = String.join(" ",
            "SELECT",
            "pg_roles.rolname,",
            "pg_roles.rolsuper,",
            "pg_roles.rolcanlogin,",
            "pg_roles.rolinherit,",
            "pg_roles.rolcreaterole,",
            "pg_roles.rolcreatedb,",
            "pg_roles.rolreplication",
            "FROM pg_roles",
            "WHERE pg_roles.rolname = ? LIMIT 1");

    protected static final PostgresRole getRole(final Connection connection, final String rolename)
            throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ROLE_PRIVILEGES)) {
            stmt.setString(1, rolename);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SecurityException("role '" + rolename + "' does not exist");
                }
                return new PostgresRole()
                        .setRoleName(rs.getString(1))
                        .setSuperUser(rs.getBoolean(2))
                        .setLoginRole(rs.getBoolean(3))
                        .setInherit(rs.getBoolean(4))
                        .setCreateRole(rs.getBoolean(5))
                        .setCreateDbRole(rs.getBoolean(6))
                        .setReplicationRole(rs.getBoolean(7));
            }
        }
    }
}
