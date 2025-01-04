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

import static org.adonix.postrise.PostgresDataSource.POSTGRES_DEFAULT_HOST;
import static org.adonix.postrise.PostgresDataSource.POSTGRES_DEFAULT_PORT;
import static org.adonix.postrise.security.postgres.SecurityProviders.DEFAULT_SECURITY;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.security.SecurityEventListener;

public class PostgresServer extends PostriseServer {

    @Override
    public String getHostName() {
        return POSTGRES_DEFAULT_HOST;
    }

    @Override
    public Integer getPort() {
        return POSTGRES_DEFAULT_PORT;
    }

    @Override
    protected ConnectionProvider getConnectionProvider(final String database) {
        return new PostgresDataSource(this, database);
    }

    @Override
    protected void setRole(final Connection connection, final String role) throws SQLException {
        RoleDAO.setRole(connection, role);
    }

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DEFAULT_SECURITY;
    }
}
