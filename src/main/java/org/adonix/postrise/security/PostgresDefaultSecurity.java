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

/**
 * The {@code DefaultSecurity} class provides default security checks
 * for user logins and connections in a database environment.
 * <p>
 * It implements the {@link SecurityEventListener} interface, performing
 * specific validation for users and roles using SQL queries on the
 * PostgreSQL system catalog.
 * </p>
 */
class PostgresDefaultSecurity implements SecurityEventListener {

    /**
     * PostgreSQL specific query to SELECT privileges for a role from the pg_roles
     * TABLE.
     */
    protected static final String SQL_SELECT_ROLE_PRIVILEGES = String.join(" ",
            "SELECT",
            "pg_roles.rolsuper AS is_super_user,",
            "pg_roles.rolcanlogin AS is_login_user",
            "FROM pg_roles",
            "WHERE pg_roles.rolname = ? LIMIT 1");

    /**
     * Constructs a new {@code DefaultSecurity} instance.
     */
    protected PostgresDefaultSecurity() {
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SecurityException if the user does not exist, is not a login user, or
     *                           is a super user.
     */
    @Override
    public void onLogin(final Connection connection, final String user) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ROLE_PRIVILEGES)) {
            stmt.setString(1, user);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SecurityException("user '" + user + "' does not exist");
                }
                if (!rs.getBoolean("is_login_user")) {
                    throw new SecurityException("user '" + user + "' is not a login user");
                }
                if (rs.getBoolean("is_super_user")) {
                    throw new SecurityException("user '" + user + "' is a super user");
                }
            }
        }
    }

    @Override
    public void onConnection(final Connection connection, final String role) throws SQLException {
        // TODO: maybe check if role is secure.
    }
}
