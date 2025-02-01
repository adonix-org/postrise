package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.adonix.postrise.security.PostgresRole;
import org.adonix.postrise.security.PostgresRoleDAO;
import org.adonix.postrise.security.RoleSecurityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

class TestCases extends TestEnvironment {

    @Test
    void t01() throws SQLException {
        try (final Connection connection = getServerInstance(AlphaServer.class)
                .getConnection("postrise", "postrise")) {
            assertNotNull(connection);
        }
    }

    @Test
    void t02() throws SQLException {
        try (final Connection connection = getServerInstance(DeltaServer.class)
                .getConnection("database_delta", "delta_application")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Super User Security Exception")
    @Test
    void t03() {
        final Server server = getServerInstance(GammaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof GammaServer);

        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("database_beta", "postrise");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: postrise is a super user", cause.getMessage());
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
            server.getConnection("not_a_database", "postrise");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
    }

    @DisplayName("Postgres Role Query")
    @Test
    void t05() throws SQLException {
        final Server server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        try (final Connection connection = server.getConnection("postrise", "postrise")) {
            PostgresRole role = PostgresRoleDAO.getRole(connection, "postrise");
            assertTrue(role.isSuperUser());
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
            server.getConnection("postrise", "non_existent_role");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: postrise is a super user", cause.getMessage());
    }

    @DisplayName("Connection Role Matches Login Role Exception")
    @Test
    void t07() {
        final Server server = getServerInstance(BetaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof BetaServer);

        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection("database_beta", "beta_login");
        });
        assertEquals("SECURITY: beta_login is the LOGIN role", t.getMessage());
    }

    @DisplayName("Postgres TCP Keep Alive")
    @Test
    void t08() {
        final PostriseServer server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        final Optional<DataSourceContext> dataSource = server.getDataSource("postrise");
        assertTrue(dataSource.isPresent());

        final String tcpKeepAlive = dataSource.get().getDataSourceProperties().getProperty("tcpKeepAlive");
        assertNotNull(tcpKeepAlive);
        assertTrue(Boolean.parseBoolean(tcpKeepAlive));
    }
}
