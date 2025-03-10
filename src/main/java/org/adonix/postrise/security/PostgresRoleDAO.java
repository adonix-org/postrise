/*
 * Copyright (C) 2025 Ty Busby
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object provides PostgreSQL {@code ROLE} functionality.
 */
public abstract class PostgresRoleDAO {

    /**
     * Private constructor. Access to {@code PostgresRoleDAO} methods must be done
     * statically.
     */
    private PostgresRoleDAO() {
    }

    /**
     * PostgreSQL specific statement to {@code SET ROLE} on the {@link Connection}.
     */
    private static final String SQL_SET_ROLE = "SELECT set_config('ROLE', ?, false)";

    /**
     * Sets the {@code ROLE} on a {@link Connection}.
     * 
     * @param connection - the {@link Connection} on which the {@code ROLE} will be
     *                   set.
     * @param roleName   - the {@code ROLE} to be set.
     * @throws SQLException if a database access or {@code ROLE} error occurs.
     */
    public static final void setRole(final Connection connection, final String roleName) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SET_ROLE)) {
            stmt.setString(1, roleName);
            stmt.execute();
        }
    }

    /**
     * PostgreSQL specific SQL to {@code RESET ROLE} on the {@link Connection}
     */
    private static final String SQL_RESET_ROLE = "RESET ROLE";

    /**
     * Reset the PostgreSQL {@code ROLE} for this {@link Connection}.
     * 
     * @param connection - the connection on which to {@code RESET ROLE}.
     * @throws SQLException if a database access error occurs.
     */
    public static final void resetRole(final Connection connection) throws SQLException {
        try (final Statement stmt = connection.createStatement()) {
            stmt.execute(SQL_RESET_ROLE);
        }
    }

    /**
     * PostgreSQL specific query to {@code SELECT} privileges for a {@code ROLE}
     * from the pg_roles view.
     */
    private static final String SQL_SELECT_ROLE_PRIVILEGES = String.join(" ",
            "SELECT",
            "pg_roles.rolname,",
            "pg_roles.rolsuper,",
            "pg_roles.rolcanlogin,",
            "pg_roles.rolinherit,",
            "pg_roles.rolcreaterole,",
            "pg_roles.rolcreatedb,",
            "pg_roles.rolreplication,",
            "pg_roles.rolconnlimit",
            "FROM pg_roles",
            "WHERE pg_roles.rolname = ? LIMIT 1");

    /**
     * Get the specified {@code ROLE} from the pg_roles view.
     * 
     * @param connection - an open PostgreSQL {@link Connection}.
     * @param roleName   - the name of the {@code ROLE} to {@code SELECT} from the
     *                   {@code pg_roles} view.
     * @return a populated {@link PostgresRole} from the {@code pg_roles} view.
     * @throws RoleSecurityException if the {@code ROLE} does not exist.
     * @throws SQLException          if a database access error occurs.
     * @see <a href=
     *      "https://www.postgresql.org/docs/current/view-pg-roles.html">pg_roles</a>
     */
    public static final PostgresRole getRole(final Connection connection, final String roleName)
            throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ROLE_PRIVILEGES)) {
            stmt.setString(1, roleName);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RoleSecurityException("role \"" + roleName + "\" does not exist");
                }
                return new PostgresRole()
                        .setRoleName(rs.getString(1))
                        .setSuperUser(rs.getBoolean(2))
                        .setLoginRole(rs.getBoolean(3))
                        .setInherit(rs.getBoolean(4))
                        .setCreateRole(rs.getBoolean(5))
                        .setCreateDbRole(rs.getBoolean(6))
                        .setReplicationRole(rs.getBoolean(7))
                        .setConnectionLimit(rs.getInt(8));
            }
        }
    }
}
