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
import org.adonix.postrise.security.PostgresRoleDAO;

/**
 * The default PostgreSQL data source implementation.
 */
final class PostgresDataSourceDefault extends PostgresDataSource {

    /**
     * The package-private constructor.
     * 
     * @param server       - the parent of this data source.
     * @param databaseName - name of the PostgreSQL database (case-sensitive).
     */
    PostgresDataSourceDefault(final PostgresServer server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public Connection getConnection(final String roleName) throws SQLException {
        Guard.check("roleName", roleName);
        final Connection connection = super.getConnection();
        try {
            getRoleSecurity().onSetRole(connection, roleName);
            PostgresRoleDAO.setRole(connection, roleName);
            return connection;
        } catch (final Exception e) {
            connection.close();
            throw e;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection();
        try {
            resetRole(connection);
            return connection;
        } catch (final Exception e) {
            connection.close();
            throw e;
        }
    }

    /**
     * Any {@link Connection} returned to the pool retains previous {@code SET ROLE}
     * which could cause unexpected permission errors when the connection is
     * re-used. {@code RESET ROLE} when getting a {@link Connection} from the pool
     * unless a {@code ROLE} is provided via
     * {@link #getConnection(String roleName)}.
     * 
     * @see #getConnection()
     * @see <a href=
     *      "https://github.com/brettwooldridge/HikariCP/wiki/Pool-Analysis">HikariCP
     *      Pool Analysis</a>
     */
    private void resetRole(final Connection connection) throws SQLException {
        PostgresRoleDAO.resetRole(connection);
    }
}
