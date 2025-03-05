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

import static org.adonix.postrise.security.RoleSecurityProvider.DISABLE_ROLE_SECURITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import nl.altindag.log.LogCaptor;

import org.adonix.postrise.servers.PostgresContainer;
import org.adonix.postrise.servers.PostriseListener;
import org.adonix.postrise.servers.StaticPortServer;
import org.adonix.postrise.servers.TestDatabaseCreator;
import org.adonix.postrise.servers.TestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

class TestEdgeCases {

    private static final LogCaptor LOG_CAPTOR = LogCaptor.forClass(PostriseServer.class);

    @DisplayName("Server Restart And Recovery")
    @Test
    void testServerRestartAndRecovery() throws Exception {

        try (final PostgresContainer server = new StaticPortServer()) {

            final DataSourceContext context = server.getDataSource(PostgresContainer.DB_NAME);
            try (final Connection connection = context.getConnection();
                    final ResultSet rs = connection.createStatement().executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
                server.logStatus();
            }

            server.stopContainer();

            assertThrows(PSQLException.class, context::getConnection);

            server.logStatus();
            server.startContainer();

            try (final Connection connection = context.getConnection();
                    final ResultSet rs = connection.createStatement().executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }

            server.logStatus();
        }
    }

    @DisplayName("NULL Action Sent To RunCatch")
    @Test
    void testNullActionSentToRunCatch() {
        try (final PostgresServer server = new PostgresServer()) {
            assertThrows(IllegalArgumentException.class, () -> server.runCatch(null));
        }
    }

    @DisplayName("Server Throws From Exception Handler")
    @Test
    void testServerThrowsFromExceptionHandler() {
        try (final PostgresServer server = new PostgresServer() {

            @Override
            protected void onException(final Exception e) {
                super.onException(e);
                throw new RuntimeException("Do not throw exceptions from here");
            }

            @Override
            public String toString() {
                return "OnExceptionServer";
            }
        }) {
            server.runCatch(() -> {
                throw new RuntimeException("Throw from runCatch()");
            });
        }
        assertThat(LOG_CAPTOR.getErrorLogs())
                .contains("OnExceptionServer: java.lang.RuntimeException: Throw from runCatch()");
        assertThat(LOG_CAPTOR.getErrorLogs())
                .contains("OnExceptionServer: java.lang.RuntimeException: Do not throw exceptions from here");
    }

    @DisplayName("Data Source Context After Server Close")
    @Test
    void testDataSourceContextAfterServerClosed() {
        final Server server = new StaticPortServer();
        final DataSourceContext context = server.getDataSource(PostgresContainer.DB_NAME);
        server.close();
        assertThrows(SQLException.class, context::getConnection);
    }

    @DisplayName("Add Data Source Listener After Server Close")
    @Test
    void testAddLDataSourceListenerAfterServerClose() {
        final Server server = new PostgresServer();
        final DataSourceListener listener = new DataSourceListener() {
        };
        server.close();
        final Throwable t = assertThrows(IllegalStateException.class,
                () -> server.addListener(listener));
        assertEquals("PostgresServer is closed", t.getMessage());
    }

    @DisplayName("Add Listener During Server Close")
    @Test
    void testAddListenerDuringServerClose() {
        try (final Server server = new StaticPortServer()) {
            server.getDataSource(PostgresContainer.DB_NAME);
            final DataSourceListener listener = new DataSourceListener() {
            };
            server.addListener(new DataSourceListener() {
                @Override
                public void beforeClose(final DataSourceContext context) {
                    final RuntimeException e = assertThrows(IllegalStateException.class,
                            () -> server.addListener(listener));
                    assertEquals("StaticPortServer is closing", e.getMessage());
                    throw e;
                }
            });
        }
    }

    @DisplayName("Before Create Exception")
    @Test
    void testBeforeCreateException() {
        try (final Server server = new StaticPortServer()) {
            server.addListener(new DataSourceListener() {
                @Override
                public void beforeCreate(DataSourceSettings settings) {
                    throw new RuntimeException("Before create exception");
                }
            });
            final Throwable t = assertThrows(CreateDataSourceException.class,
                    () -> server.getDataSource(PostgresContainer.DB_NAME));

            final Throwable cause = t.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof RuntimeException);
            assertEquals("Before create exception", cause.getMessage());
        }
    }

    @DisplayName("No Database Listeners")
    @Test
    void testNoDatabaseListeners() throws SQLException {
        try (final Server server = new TestServer() {
            @Override
            public void beforeCreate(final DataSourceSettings settings) {
                super.beforeCreate(settings);
                settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
            }
        }) {
            assertNotNull(server.getDataSource(TestDatabaseCreator.createTestDatabase(server)));
        }
    }

    @DisplayName("Server Close Idempotency")
    @Test
    void testServerCloseIdempotency() {
        try (final Server server = new PostgresServer()) {
            server.close();
        }
        assertThat(LOG_CAPTOR.getWarnLogs()).contains("PostgresServer: extra close request ignored");
    }

    @DisplayName("Server Get Empty Database Names")
    @Test
    void testServerGetEmptyDatabaseNames() {
        try (final Server server = new PostgresServer()) {
            assertEquals(0, server.getDatabaseNames().size());
        }
    }

    @DisplayName("Server Add Database Listener Twice")
    @Test
    void testServerAddDatabaseListenerTwice() {
        try (final Server server = new PostgresServer()) {
            final DatabaseListener listener = new PostriseListener();
            server.addListener(listener);
            server.addListener(listener);
        }
        assertThat(LOG_CAPTOR.getErrorLogs())
                .contains("PostgresServer: Database listener \"postrise\" already exists");
    }

    @DisplayName("Server Add Data Source Listener Twice")
    @Test
    void testServerAddDataSourceListenerTwice() {
        try (final PostriseServer server = new PostgresServer()) {
            server.addListener(server);
            assertThat(LOG_CAPTOR.getErrorLogs())
                    .contains("PostgresServer: Data source listener \"PostgresServer\" already exists");
        }
    }

    @DisplayName("Invalid Pool Status Request")
    @Test
    void testInvalidPoolStatusRequest() {
        try (final Server server = new StaticPortServer()) {
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

    @DisplayName("Test Empty Listener")
    @Test
    void testEmptyListener() {
        try (final Server server = new StaticPortServer()) {
            server.addListener(new DataSourceListener() {
            });
            assertThrows(CreateDataSourceException.class, () -> server.getConnection("database"));
        }
    }

    @DisplayName("Roles Not Supported")
    @Test
    void testRolesNotSupported() {
        try (final PostgresServer server = new StaticPortServer() {

            @Override
            protected PostgresDataSource createDataSource(String databaseName) {
                return new PostgresDataSourceNoRoles(this, databaseName);
            }
        }) {
            final Throwable t = assertThrows(UnsupportedOperationException.class,
                    () -> server.getConnection(PostgresContainer.DB_NAME, "no_roles"));
            assertEquals(PostgresDataSourceNoRoles.class.getSimpleName() + " does not support roles",
                    t.getMessage());
        }
    }

    @AfterEach
    void afterEach() {
        LOG_CAPTOR.clearLogs();
    }

    @AfterAll
    static void afterAll() {
        LOG_CAPTOR.close();
    }
}
