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

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_DEFAULT_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_STRICT_ROLE_SECURITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.adonix.postrise.security.PostgresRole;
import org.adonix.postrise.security.PostgresRoleDAO;
import org.adonix.postrise.servers.PostgresDocker;
import org.adonix.postrise.servers.TestDatabaseListener;
import org.adonix.postrise.servers.TestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestBasicOperation {

    private static final PostgresDocker server = new TestServer();

    @DisplayName("Default Security LOGIN")
    @Test
    void testDefaultSecurityLogin() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Default Security SET ROLE")
    @Test
    void testDefaultSecuritySetRole() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_no_super")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Default Security Connection Limit")
    @Test
    void testDefaultSecurityConnectionLimit() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "connection_limited");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Strict Security SET ROLE")
    @Test
    void testStrictSecuritySetRole() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_no_super")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Default Security Connection Limit Large")
    @Test
    void testDefaultSecurityConnectionLimitLarge() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "connection_limited_large");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Disabled Security SUPERUSER SET ROLE")
    @Test
    void testDisableSecuritySuperUserSetRole() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, DISABLE_ROLE_SECURITY,
                "with_login_with_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "with_login_no_super")) {
            assertNotNull(connection);
        }
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "with_login_with_super")) {
            assertNotNull(connection);
        }
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_with_super")) {
            assertNotNull(connection);
        }
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_no_super")) {
            assertNotNull(connection);
        }
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), PostgresDocker.DB_USER)) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Disabled Security NO SUPERUSER SET ROLE")
    @Test
    void testDisableSecurityNoSuperUserSetRole() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, DISABLE_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "with_login_no_super")) {
            assertNotNull(connection);
        }
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "with_login_with_super")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Connection Reset")
    @Test
    void testConnectionRoleReset() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        // Set the max pool size to 1. Only one connection in the pool.
        final DataSourceContext context = server.getDataSource(listener.getDatabaseName());
        context.setMaxPoolSize(1);
        assertEquals(context.getTotalConnections(), 1);

        // Set the single connection in the pool to no_login_no_super role.
        try (final Connection connection = context.getConnection("no_login_no_super")) {
            assertNotNull(connection);

            // Set the single connection auto commit to false to verify reset.
            assertTrue(connection.getAutoCommit());
            connection.setAutoCommit(false);
            assertFalse(connection.getAutoCommit());
        }

        // Get the single connection and verify the current_user has reverted to
        // the postrise role and auto commit reverted to true.
        try (final Connection connection = context.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT session_user, current_user");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("with_login_no_super", rs.getString(1));
            assertEquals("with_login_no_super", rs.getString(2));
            assertTrue(connection.getAutoCommit());
        }
    }

    @DisplayName("ROLE Query")
    @Test
    void testRoleQuery() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            final PostgresRole role = PostgresRoleDAO.getRole(connection, "no_login_no_super");
            assertFalse(role.isCreateRole());
            assertFalse(role.isCreateDbRole());
            assertFalse(role.isInheritRole());
            assertFalse(role.isLoginRole());
            assertFalse(role.isReplicationRole());
            assertFalse(role.isSuperUser());
            assertEquals(role.getConnectionLimit(), -1);
        }
    }

    @DisplayName("Get Connection From Server With ROLE")
    @Test
    void testGetConnectionFromServerWithRole() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_no_super");
                PreparedStatement stmt = connection.prepareStatement("SELECT session_user, current_user");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("with_login_no_super", rs.getString(1));
            assertEquals("no_login_no_super", rs.getString(2));
        }
    }

    @DisplayName("Get Connection From DataSourceContext With ROLE")
    @Test
    void testGetConnectionFromContextWithRole() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final DataSourceContext context = server.getDataSource(listener.getDatabaseName());
        try (final Connection connection = context.getConnection("no_login_no_super");
                PreparedStatement stmt = connection.prepareStatement("SELECT session_user, current_user");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("with_login_no_super", rs.getString(1));
            assertEquals("no_login_no_super", rs.getString(2));
        }
    }

    @DisplayName("Get Max Postgres Connections")
    @Test
    void testGetMaxConnections() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final DataSourceContext context = server.getDataSource(listener.getDatabaseName());
        try (final Connection connection = context.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT current_setting('max_connections')");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(rs.getInt(1), PostgresDocker.MAX_CONNECTIONS);
        }
    }

    @DisplayName("Postgres TCP Keep Alive Property Set")
    @Test
    void testTcpKeepAlivePropertySet() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final DataSourceContext dataSource = server.getDataSource(listener.getDatabaseName());
        assertNotNull(dataSource);

        final String tcpKeepAlive = dataSource.getDataSourceProperties().getProperty("tcpKeepAlive");
        assertNotNull(tcpKeepAlive);
        assertTrue(Boolean.parseBoolean(tcpKeepAlive));
    }

    @DisplayName("Postgres Server Validate Default Host Name and Port")
    @Test
    void testPostgresServerDefaultHostNameAndPort() throws SQLException {
        try (final Server server = new PostgresServer()) {
            assertEquals(server.getHostName(), PostgresServer.POSTGRES_DEFAULT_HOSTNAME);
            assertEquals(server.getPort(), PostgresServer.POSTGRES_DEFAULT_PORT);
        }
    }

    @DisplayName("Data Source Getters and Setters")
    @Test
    void testDataSourceGettersAndSetters() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final DataSourceContext dataSource = server.getDataSource(listener.getDatabaseName());
        assertNotNull(dataSource);

        getAndSet(45000L, dataSource::getConnectionTimeout, dataSource::setConnectionTimeout);
        getAndSet(45000L, dataSource::getIdleTimeout, dataSource::setIdleTimeout);
        getAndSet(45000L, dataSource::getLeakDetectionThreshold, dataSource::setLeakDetectionThreshold);
        getAndSet(45000L, dataSource::getMaxLifetime, dataSource::setMaxLifetime);
        getAndSet(15, dataSource::getMaxPoolSize, dataSource::setMaxPoolSize);
        getAndSet(8, dataSource::getMinIdle, dataSource::setMinIdle);
        getAndSet(45000L, dataSource::getValidationTimeout, dataSource::setValidationTimeout);

        assertTrue(dataSource.isAutoCommit());
    }

    static final <T> void getAndSet(final T newValue, final Supplier<T> getter, final Consumer<T> setter) {
        assertNotNull(newValue);
        assertNotEquals(newValue, getter.get());
        setter.accept(newValue);
        assertNotNull(getter.get());
        assertEquals(getter.get(), newValue);
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        server.startContainer();
        TestEnvironment.initialize(server);
    }

    @AfterAll
    static void afterAll() {
        server.close();
        server.stopContainer();
    }
}
