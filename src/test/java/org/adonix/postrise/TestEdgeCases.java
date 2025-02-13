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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.adonix.postrise.servers.EdgeCaseServer;
import org.adonix.postrise.servers.PostgresDocker;
import org.adonix.postrise.servers.StaticPortServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class TestEdgeCases {

    @DisplayName("Server Restart And Recovery")
    @Test
    void testServerRestartAndRecovery() throws Exception {

        try (final PostgresDocker server = new StaticPortServer()) {

            server.startContainer();
            final DataSourceContext context = server.getDataSource(PostgresDocker.DB_NAME);
            try (final Connection connection = context.getConnection();
                    final ResultSet rs = connection.createStatement().executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(rs.getInt(1), 1);
            }

            server.stopContainer();
            assertThrows(PSQLException.class, context::getConnection);

            server.startContainer();

            try (final Connection connection = context.getConnection();
                    final ResultSet rs = connection.createStatement().executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(rs.getInt(1), 1);
            }

            server.stopContainer();
        }
    }

    @DisplayName("Server Close Idempotency")
    @Test
    void testServerCloseIdempotency() throws SQLException {
        final Server server = new PostgresServer();
        server.close();
        server.close();
        server.close();
    }

    @DisplayName("Server Get Empty Database Names")
    @Test
    void testServerGetEmptyDatabaseNames() throws SQLException {
        try (final Server server = new EdgeCaseServer()) {
            assertEquals(server.getDatabaseNames().size(), 0);
        }
    }

    @DisplayName("Invalid Pool Status Request")
    @Test
    void testInvalidPoolStatusRequest() throws SQLException {
        try (final Server server = new EdgeCaseServer()) {
            server.addListener(new DataSourceListener() {
                @Override
                public void beforeCreate(final DataSourceSettings settings) {
                    final DataSourceContext context = (DataSourceContext) settings;
                    final Throwable t = assertThrows(IllegalStateException.class, context::getActiveConnections);
                    assertEquals(t.getMessage(), "Pool status request is invalid");
                }
            });
            assertThrows(CreateDataSourceException.class, () -> server.getConnection("database"));
        }
    }
}
