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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import org.adonix.postrise.Server;

public abstract class TestDatabaseCreator {

    private static final AtomicInteger databaseNameIndex = new AtomicInteger();

    private TestDatabaseCreator() {
    }

    public static final String createTestDatabase(final Server server) throws SQLException {
        final String databaseName = getDatabaseName();
        try (final Connection connection = server.getConnection(PostgresContainer.DB_NAME);
                final Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(getCreateDatabaseSql(databaseName));
        }
        return databaseName;
    }

    private static final String getDatabaseName() {
        return String.format("database_%03d", databaseNameIndex.incrementAndGet());
    }

    private static final String getCreateDatabaseSql(final String databaseName) {
        return String.join(" ",
                "CREATE DATABASE",
                databaseName,
                "WITH ENCODING = 'UTF8'",
                "TABLESPACE = pg_default",
                "CONNECTION LIMIT = -1",
                "IS_TEMPLATE = False");
    }
}
