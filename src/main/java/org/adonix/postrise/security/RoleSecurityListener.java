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
import org.adonix.postrise.DataSourceContext;

/**
 * Implementations will validate each security event.
 */
public interface RoleSecurityListener extends EventListener {

    /**
     * After creating a {@link DataSourceContext}, the server will provide a
     * {@link Connection} which can be used to validate the login user.
     * 
     * @param context    - the new data source.
     * @param connection - the connection to be used for validation.
     * @throws SQLException a database or {@link RoleSecurityException} has
     *                      occurred.
     */
    void onLogin(DataSourceContext context, Connection connection) throws SQLException;

    default void onSetRole(DataSourceContext context, Connection connection, String roleName) throws SQLException {
    }
}
