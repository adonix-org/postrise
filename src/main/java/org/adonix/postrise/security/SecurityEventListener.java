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
 * An interface for handling security related events. Implementations of this
 * interface can perform custom logic for validating users and roles during
 * login and connection requests.
 */
public interface SecurityEventListener extends EventListener {

    /**
     * This security event fires each time a new data source is created.
     * 
     * @param connection - use this {@link Connection} to validate the login user.
     * @param user       - the login user to be validated.
     * @throws SQLException if a SQL error occurs validating the login user.
     */
    void onLogin(Connection connection, String user) throws SQLException;

    /**
     * This security event fires each time a connection is requested.
     * 
     * @param connection - use this {@link Connection} to validate the given role.
     * @param role       - the role to be validated.
     * @throws SQLException if a SQL error occurs validating the role for this
     *                      connection.
     */
    void onConnection(Connection connection, String role) throws SQLException;
}
