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
 * Implementations authorize a provided {@code ROLE} during security events.
 */
public interface RoleSecurityListener extends EventListener {

    /**
     * After successfully creating a new {@link DataSourceContext}, the server will
     * provide a {@link Connection} for authorizing the {@code LOGIN} user. If an
     * exception is thrown, the data source is immediately closed.
     * 
     * @param context    - the new {@link DataSourceContext}.
     * @param connection - {@link Connection} used for authorization.
     * @throws SQLException if a database access error occurs.
     * @see DataSourceContext#getUsername()
     */
    void onLogin(DataSourceContext context, Connection connection) throws SQLException;

    /**
     * This event is dispatched after requesting a {@link Connection} with a
     * specific {@code ROLE}.
     * 
     * @param connection - {@link Connection} used for authorization.
     * @param roleName   - the {@code ROLE} to be authorized.
     * @throws SQLException if a database access error occurs.
     */
    default void onSetRole(Connection connection, String roleName) throws SQLException {
    }
}
