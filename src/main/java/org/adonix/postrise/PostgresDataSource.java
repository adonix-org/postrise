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

import static org.adonix.postrise.security.RoleSecurityProvider.POSTGRES_DEFAULT_ROLE_SECURITY;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.security.PostgresRoleDAO;
import org.adonix.postrise.security.RoleSecurityListener;

public class PostgresDataSource extends PostriseDataSource {

    private static final String JDBC_POSTGRES_PREFIX = "jdbc:postgresql://";

    protected PostgresDataSource(final Server server, final String databaseName) {
        super(server, databaseName);
        addDataSourceProperty("tcpKeepAlive", "true");
    }

    @Override
    final String getJdbcUrl(final Server server) {
        return JDBC_POSTGRES_PREFIX + server.getHostName() + ":" + server.getPort() + "/" + getDatabaseName();
    }

    @Override
    protected RoleSecurityListener getDefaultRoleSecurity() {
        return POSTGRES_DEFAULT_ROLE_SECURITY;
    }

    @Override
    public final Connection getConnection(final String roleName) throws SQLException {
        Guard.check("roleName", roleName);
        final Connection connection = super.getConnection();
        try {
            getRoleSecurity().onSetRole(this, connection, roleName);
            PostgresRoleDAO.setRole(connection, roleName);
            return connection;
        } catch (final Exception e) {
            connection.close();
            throw e;
        }
    }

    @Override
    public final Connection getConnection() throws SQLException {
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
    protected void resetRole(final Connection connection) throws SQLException {
        PostgresRoleDAO.resetRole(connection);
    }
}
