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
import java.sql.SQLException;
import java.util.EventListener;

/**
 * An interface for handling security related events.
 * <p>
 * Implementations of this interface can perform custom logic for validating
 * roles during login and connection requests.
 */
public interface RoleSecurityListener extends EventListener {

    /**
     * This security event fires each time a new
     * {@link org.adonix.postrise.ConnectionProvider ConnectionProvider} is created.
     * 
     * @param connection - use this {@link Connection} to validate the login role.
     * @param roleName   - the login role to be validated when creating the
     *                   {@link org.adonix.postrise.ConnectionProvider
     *                   ConnectionProvider}.
     * @throws SQLException if a SQL error occurs validating the login role.
     */
    void onLogin(Connection connection, String roleName) throws SQLException;

    /**
     * This security event fires each time a {@link Connection} is requested from
     * a {@link org.adonix.postrise.ConnectionProvider ConnectionProvider}.
     * 
     * @param connection - the {@link Connection} to validate the given role.
     * @param roleName   - the role to be validated for this {@link Connection}
     *                   request.
     * @throws SQLException if a SQL error occurs validating the role for this
     *                      connection.
     */
    void onConnection(Connection connection, String roleName) throws SQLException;
}
