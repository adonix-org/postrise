package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.PostgresRole;
import org.adonix.postrise.security.PostgresRoleDAO;
import org.adonix.postrise.security.RoleSecurityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

class TestCases extends TestEnvironment {

    @Test
    void run1() throws SQLException {
        try (final Connection connection = getServerInstance(AlphaServer.class)
                .getConnection("postrise", "postrise")) {
            assertNotNull(connection);
        }
    }

    @Test
    void run2() throws SQLException {
        try (final Connection connection = getServerInstance(DeltaServer.class)
                .getConnection("database_delta", "delta_application")) {
            assertNotNull(connection);
        }
    }

    @DisplayName("Super User Security Exception")
    @Test
    void run3() {
        final Server server = getServerInstance(GammaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof GammaServer);

        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("database_beta", "postrise");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: role 'postrise' is a super user", cause.getMessage());
    }

    /**
     * This test validates that {@link PSQLException} errors are propagated when
     * they occur.
     */
    @DisplayName("Postgres Exception Propagation")
    @Test
    void run4() {
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
    void run5() throws SQLException {
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
    void run6() throws SQLException {
        final Server server = getServerInstance(GammaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof GammaServer);

        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("postrise", "non_existent_role");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: role 'postrise' is a super user", cause.getMessage());
    }
}
