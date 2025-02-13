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
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import org.adonix.postrise.servers.EdgeCaseServer;
import org.adonix.postrise.servers.PostgresContainer;
import org.adonix.postrise.servers.PostriseListener;
import org.adonix.postrise.servers.StaticPortServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestEdgeCases {

    @DisplayName("Server Restart And Recovery")
    @Test
    void testServerRestartAndRecovery() throws Exception {

        try (final PostgresContainer server = new StaticPortServer()) {

            server.startContainer();
            final DataSourceContext context = server.getDataSource(PostgresContainer.DB_NAME);
            try (final Connection connection = context.getConnection();
                    final ResultSet rs = connection.createStatement().executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
                server.logStatus();
            }

            server.stopContainer();

            server.logStatus();
            server.startContainer();

            try (final Connection connection = context.getConnection();
                    final ResultSet rs = connection.createStatement().executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }

            server.logStatus();
            server.stopContainer();
        }
    }

    @DisplayName("Server Close Idempotency")
    @Test
    void testServerCloseIdempotency() {
        final Server server = new PostgresServer();
        assertNotNull(server);
        server.close();
        server.close();
        server.close();
    }

    @DisplayName("Server Get Empty Database Names")
    @Test
    void testServerGetEmptyDatabaseNames() {
        try (final Server server = new EdgeCaseServer()) {
            assertEquals(0, server.getDatabaseNames().size());
        }
    }

    @DisplayName("Server Add Listener Twice")
    @Test
    void testServerAddListenerTwice() {
        try (final Server server = new EdgeCaseServer()) {
            assertNotNull(server);
            final DatabaseListener listener = new PostriseListener();
            server.addListener(listener);
            server.addListener(listener);
        }
    }

    @DisplayName("Invalid Pool Status Request")
    @Test
    void testInvalidPoolStatusRequest() {
        try (final Server server = new EdgeCaseServer()) {
            server.addListener(new DataSourceListener() {
                @Override
                public void beforeCreate(final DataSourceSettings settings) {
                    final DataSourceContext context = (DataSourceContext) settings;
                    final Throwable t = assertThrows(IllegalStateException.class, context::getActiveConnections);
                    assertEquals("Pool status request is invalid", t.getMessage());
                }
            });
            assertThrows(CreateDataSourceException.class, () -> server.getConnection("database"));
        }
    }
}
