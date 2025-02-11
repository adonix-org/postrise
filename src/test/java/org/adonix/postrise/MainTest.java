package org.adonix.postrise;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_DEFAULT_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_STRICT_ROLE_SECURITY;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.adonix.postrise.security.PostgresRole;
import org.adonix.postrise.security.PostgresRoleDAO;
import org.adonix.postrise.security.RoleSecurityException;
import org.adonix.postrise.servers.PostgresDocker;
import org.adonix.postrise.servers.TestDatabaseListener;
import org.adonix.postrise.servers.TestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class MainTest {

    private static final Server server = TestServer.getInstance();

    @DisplayName("EMPTY Database Name")
    @Test
    void testEmptyDatabaseName() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(" ");
        });
        assertEquals("Illegal EMPTY String for databaseName", t.getMessage());
    }

    @DisplayName("NULL Database Name")
    @Test
    void testNullDatabaseName() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(null);
        });
        assertEquals("Illegal NULL String for databaseName", t.getMessage());
    }

    @DisplayName("EMPTY ROLE String")
    @Test
    void testEmptyRoleString() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(listener.getDatabaseName(), " ");
        });
        assertEquals(t.getMessage(), "Illegal EMPTY String for roleName");
    }

    @DisplayName("NULL ROLE String")
    @Test
    void testNullRoleString() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(listener.getDatabaseName(), null);
        });
        assertEquals(t.getMessage(), "Illegal NULL String for roleName");
    }

    @DisplayName("NOLOGIN Exception")
    @Test
    void testNoLoginException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "no_login_no_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: role \"no_login_no_super\" is not permitted to log in", cause.getMessage());
    }

    @DisplayName("NOLOGIN SUPERUSER Exception")
    @Test
    void testNoLoginSuperUserException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "no_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: role \"no_login_with_super\" is not permitted to log in", cause.getMessage());
    }

    @DisplayName("Default Security SUPERUSER LOGIN Exception")
    @Test
    void testDefaultSecuritySuperUserLoginException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: with_login_with_super is a SUPERUSER role", cause.getMessage());
    }

    @DisplayName("Default Security LOGIN No Exception")
    @Test
    void testDefaultSecurityLoginNoException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Default Security SET ROLE No Exception")
    @Test
    void testDefaultSecuritySetRoleNoException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_no_super")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Strict Security LOGIN SUPERUSER Exception")
    @Test
    void testStrictSecurityLoginSuperUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: with_login_with_super is a SUPERUSER role", cause.getMessage());
    }

    @DisplayName("Strict Security SET ROLE SUPERUSER Exception")
    @Test
    void testStrictSecuritySetRoleSuperUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(listener.getDatabaseName(), "no_login_with_super");
        });
        assertEquals(t.getMessage(), "SECURITY: no_login_with_super is a SUPERUSER role");
    }

    @DisplayName("Strict Security SET ROLE LOGIN Exception")
    @Test
    void testStrictSecuritySetRoleLoginUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(listener.getDatabaseName(), "with_login_no_super");
        });
        assertEquals(t.getMessage(), "SECURITY: with_login_no_super is a LOGIN role");
    }

    @DisplayName("Strict Security SET ROLE SUPERUSER LOGIN Exception")
    @Test
    void testStrictSecuritySetRoleSuperLoginUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(listener.getDatabaseName(), "with_login_with_super");
        });
        assertEquals(t.getMessage(), "SECURITY: with_login_with_super is a SUPERUSER role");
    }

    @DisplayName("Strict Security SET ROLE No Exception")
    @Test
    void testStrictSecuritySetRoleNoException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "no_login_no_super")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Disabled Security SUPERUSER SET ROLE No Exception")
    @Test
    void testDisableSecuritySuperUserSetRoleNoException() throws SQLException {
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
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "postrise")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Disabled Security NO SUPERUSER SET ROLE No Exception")
    @Test
    void testDisableSecurityNoSuperUserSetRoleNoException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, DISABLE_ROLE_SECURITY,
                "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "with_login_no_super")) {
            assertNotNull(connection);
        }
        try (final Connection connection = server.getConnection(listener.getDatabaseName(), "with_login_with_super")) {
            assertNotNull(connection);
        }
    }

    /**
     * This test validates that {@link PSQLException} errors are propagated when
     * they occur.
     */
    @DisplayName("Postgres Exception Propagation")
    @Test
    void testPostgresExceptionPropagation() {
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("not_a_database");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
    }

    @DisplayName("Connection Reset")
    @Test
    void testConnectionRoleReset() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        // Set the max pool size to 1. Only one connection in the pool.
        final DataSourceContext context = server.getDataSource(listener.getDatabaseName());
        context.setMaxPoolSize(1);
        assertEquals(context.getTotalConnections(), 1);

        // Set the single connection in the pool to the no_login_no_super role.
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
            assertFalse(role.isSuperUser());
            assertFalse(role.isLoginRole());
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
        try (final Connection connection = context.getConnection("no_login_no_super");
                PreparedStatement stmt = connection.prepareStatement("SELECT current_setting('max_connections')");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(rs.getInt(1), PostgresDocker.MAX_CONNECTIONS);
        }
    }

    @DisplayName("Postgres TCP Keep Alive Set")
    @Test
    void testTcpKeepAlivePropertySet() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final DataSourceContext dataSource = server.getDataSource(listener.getDatabaseName());
        assertNotNull(dataSource);

        final String tcpKeepAlive = dataSource.getDataSourceProperties().getProperty("tcpKeepAlive");
        assertNotNull(tcpKeepAlive);
        assertTrue(Boolean.parseBoolean(tcpKeepAlive));
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        PostgresDocker.start();
        TestEnvironment.initialize(server);
    }

    @AfterAll
    static void afterAll() {
        server.close();
        PostgresDocker.stop();
    }
}
