package org.adonix.postrise;

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
import org.adonix.postrise.servers.AlphaServer;
import org.adonix.postrise.servers.DeltaServer;
import org.adonix.postrise.servers.GammaServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

class TestCases extends TestEnvironment {

    @Test
    void t01() throws SQLException {
        try (final Connection connection = getServerInstance(AlphaServer.class)
                .getConnection("postrise")) {
            assertNotNull(connection);
        }
    }

    @Test
    void t02() throws SQLException {
        try (final Connection connection = getServerInstance(DeltaServer.class)
                .getConnection("database_delta")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("SUPERUSER Security Exception")
    @Test
    void t03() {
        final Server server = getServerInstance(GammaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof GammaServer);

        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("database_beta");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: postrise is a SUPER user", cause.getMessage());
    }

    /**
     * This test validates that {@link PSQLException} errors are propagated when
     * they occur.
     */
    @DisplayName("Postgres Exception Propagation")
    @Test
    void t04() {
        final Server server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("not_a_database");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
    }

    @DisplayName("Postgres SUPERUSER Role Query")
    @Test
    void t05() throws SQLException {
        final Server server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        try (final Connection connection = server.getConnection("postrise")) {
            PostgresRole role = PostgresRoleDAO.getRole(connection, "postrise");
            assertTrue(role.isSuperUser());
            assertTrue(role.isLoginRole());
            assertEquals(role.getConnectionLimit(), -1);
        }
    }

    @DisplayName("Postrise Database Security")
    @Test
    void t06() {
        final Server server = getServerInstance(GammaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof GammaServer);

        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("postrise");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: postrise is a SUPER user", cause.getMessage());
    }

    @DisplayName("Postgres TCP Keep Alive SUPERUSER")
    @Test
    void t08() {
        final PostriseServer server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        final DataSourceContext dataSource = server.getDataSource("postrise");
        assertNotNull(dataSource);

        final String tcpKeepAlive = dataSource.getDataSourceProperties().getProperty("tcpKeepAlive");
        assertNotNull(tcpKeepAlive);
        assertTrue(Boolean.parseBoolean(tcpKeepAlive));
    }

    @DisplayName("NULL Database SUPERUSER")
    @Test
    void t09() {
        final PostriseServer server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(null);
        });

        assertEquals("Unexpected null String for databaseName", t.getMessage());
    }

    @DisplayName("EMPTY Database String SUPERUSER")
    @Test
    void t11() throws SQLException {
        final PostriseServer server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        try (final Connection connection = server.getConnection("postrise")) {
            assertNotNull(connection);
        }

        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(" ");
        });

        assertEquals("Unexpected empty String for databaseName", t.getMessage());
    }

    @DisplayName("Postgres TCP Keep Alive Not SUPERUSER")
    @Test
    void t13() {
        final PostriseServer server = getServerInstance(DeltaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof DeltaServer);

        final DataSourceContext dataSource = server.getDataSource("database_delta");
        assertNotNull(dataSource);

        final String tcpKeepAlive = dataSource.getDataSourceProperties().getProperty("tcpKeepAlive");
        assertNotNull(tcpKeepAlive);
        assertTrue(Boolean.parseBoolean(tcpKeepAlive));
    }

    @DisplayName("PostriseDataSource Getters and Setters at Runtime")
    @Test
    void t14() {
        final PostriseServer server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        final DataSourceContext dataSource = server.getDataSource("postrise");
        assertNotNull(dataSource);

        dataSource.setMaxPoolSize(50);
        assertEquals(50, dataSource.getMaxPoolSize());

        dataSource.setMinIdle(1);
        assertEquals(1, dataSource.getMinIdle());

        assertTrue(dataSource.isAutoCommit());
    }

    @DisplayName("Check for NULL when Setting the Role")
    @Test
    void t15() throws SQLException {
        final PostriseServer server = getServerInstance(DeltaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof DeltaServer);

        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection("postrise", null);
        });
        assertEquals(t.getMessage(), "Unexpected null String for roleName");
    }

    @DisplayName("Get a Connection from a Serer with a Specified ROLE")
    @Test
    void t16() throws SQLException {
        final PostriseServer server = getServerInstance(DeltaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof DeltaServer);

        try (final Connection connection = server.getConnection("postrise", "delta_application");
                PreparedStatement stmt = connection.prepareStatement("SELECT session_user, current_user");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("delta_login", rs.getString(1));
            assertEquals("delta_application", rs.getString(2));
        }
    }

    @DisplayName("Get a Connection from a DataSourceContext with a Specified ROLE")
    @Test
    void t17() throws SQLException {
        final PostriseServer server = getServerInstance(DeltaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof DeltaServer);

        final DataSourceContext dataSource = server.getDataSource("postrise");
        try (final Connection connection = dataSource.getConnection("delta_application");
                PreparedStatement stmt = connection.prepareStatement("SELECT session_user, current_user");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("delta_login", rs.getString(1));
            assertEquals("delta_application", rs.getString(2));
        }
    }
}
