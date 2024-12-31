/*
 * Copyright (C) 2024 Ty Busby
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

public class DefaultSecurity implements SecurityEventListener {

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
                    throw new SQLException("ERROR: user '" + user + "' is not a login user");
                }
                if (rs.getBoolean("is_super_user")) {
                    throw new SQLException("ERROR: login user '" + user + "' is a super user");
                }
            }
        }
    }

    @Override
    public void onConnection(Connection connection, String role) throws SQLException {
        // TODO: maybe check if role is secure.
    }
}
