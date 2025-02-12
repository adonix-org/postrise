package org.adonix.postrise;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_DEFAULT_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_STRICT_ROLE_SECURITY;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        assertEquals(t.getMessage(), "Illegal EMPTY String for databaseName");
    }

    @DisplayName("NULL Database Name")
    @Test
    void testNullDatabaseName() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(null);
        });
        assertEquals(t.getMessage(), "Illegal NULL String for databaseName");
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

    @DisplayName("NULL Listener")
    @Test
    void testNullListener() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.addListener(null);
        });
        assertEquals(t.getMessage(), "Illegal NULL Object for listener");
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
        assertEquals(cause.getMessage(), "FATAL: role \"no_login_no_super\" is not permitted to log in");
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
        assertEquals(cause.getMessage(), "FATAL: role \"no_login_with_super\" is not permitted to log in");
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
        assertEquals(cause.getMessage(), "SECURITY: with_login_with_super is a SUPERUSER role");
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

    @DisplayName("Default Security Connection Limit No Exception")
    @Test
    void testDefaultSecurityConnectionLimitNoException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "connection_limited");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Default Security Connection Limit Large No Exception")
    @Test
    void testDefaultSecurityConnectionLimitLargeNoException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "connection_limited_large");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
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

    @DisplayName("Postgres Exception Propagation")
    @Test
    void testPostgresExceptionPropagation() {
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("not_a_database");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals(cause.getMessage(), "FATAL: database \"not_a_database\" does not exist");
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
            assertFalse(role.isCreateRole());
            assertFalse(role.isCreateDbRole());
            assertFalse(role.isInheritRole());
            assertFalse(role.isLoginRole());
            assertFalse(role.isReplicationRole());
            assertFalse(role.isSuperUser());
            assertEquals(role.getConnectionLimit(), -1);
        }
    }

    @DisplayName("ROLE Does Not Exist Exception")
    @Test
    void testRoleDoesNotExistException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            final Throwable t = assertThrows(RoleSecurityException.class, () -> {
                PostgresRoleDAO.getRole(connection, "role_does_not_exist");
            });
            assertEquals(t.getMessage(), "SECURITY: role 'role_does_not_exist' does not exist");
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

    @DisplayName("Getters and Setters")
    @Test
    void testGettersAndSetters() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final DataSourceContext dataSource = server.getDataSource(listener.getDatabaseName());
        assertNotNull(dataSource);

        getAndSet(45000L, dataSource::getConnectionTimeout, dataSource::setConnectionTimeout);
        getAndSet(45000L, dataSource::getMaxLifetime, dataSource::setMaxLifetime);
        getAndSet(45000L, dataSource::getLeakDetectionThreshold, dataSource::setLeakDetectionThreshold);
        getAndSet(45000L, dataSource::getValidationTimeout, dataSource::setValidationTimeout);
        getAndSet(6, dataSource::getMinIdle, dataSource::setMinIdle);
        getAndSet(45000L, dataSource::getIdleTimeout, dataSource::setIdleTimeout);
        getAndSet(15, dataSource::getMaxPoolSize, dataSource::setMaxPoolSize);

    }

    public static final <T> void getAndSet(final T newValue, final Supplier<T> getter, final Consumer<T> setter) {
        assertNotNull(newValue);
        assertNotEquals(newValue, getter.get());
        setter.accept(newValue);
        assertNotNull(getter.get());
        assertEquals(getter.get(), newValue);
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
