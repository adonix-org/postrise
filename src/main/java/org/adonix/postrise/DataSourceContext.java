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

package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceContext extends ConnectionPoolSettings, ConnectionPoolStatus, ConnectionSettingsRead {

    /**
     * @return A {@link Connection} to the data source.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection() throws SQLException;

    /**
     * Set the {@code ROLE} to the roleName on the {@link Connection}.
     * 
     * @param roleName - The {@code ROLE} to be set for the connection.
     * @return {@link Connection} to the data source with the {@code ROLE} set.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection(String roleName) throws SQLException;
}
