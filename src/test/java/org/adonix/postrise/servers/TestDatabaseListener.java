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

package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_DEFAULT_ROLE_SECURITY;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;
import org.adonix.postrise.Server;
import org.adonix.postrise.security.RoleSecurityListener;

public class TestDatabaseListener implements DatabaseListener {

    private static final AtomicInteger databaseNameIndex = new AtomicInteger();

    private final String username;
    private final String password;
    private final String databaseName;
    private final RoleSecurityListener security;

    public TestDatabaseListener(final Server server, final String username) throws SQLException {
        this(server, POSTGRES_DEFAULT_ROLE_SECURITY, username);
    }

    public TestDatabaseListener(final Server server, final RoleSecurityListener security, final String username)
            throws SQLException {
        this.security = security;
        this.username = username;
        this.password = "helloworld";
        this.databaseName = String.format("database_%03d", databaseNameIndex.incrementAndGet());
        createTestDatabase(server);
        server.addListener(this);
    }

    protected String getCreateDatabaseSql() {
        return String.join(" ",
                "CREATE DATABASE",
                getDatabaseName(),
                "WITH ENCODING = 'UTF8'",
                "TABLESPACE = pg_default",
                "CONNECTION LIMIT = -1",
                "IS_TEMPLATE = False");
    }

    private void createTestDatabase(final Server server) throws SQLException {
        try (final Connection connection = server.getConnection(PostgresDocker.DB_NAME);
                final Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(getCreateDatabaseSql());
        }
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setRoleSecurity(security);
        settings.setUsername(username);
        settings.setPassword(password);
        settings.setMaxPoolSize(5);
        settings.setMinIdle(5);
    }
}
