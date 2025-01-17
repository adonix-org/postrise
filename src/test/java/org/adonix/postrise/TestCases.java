package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
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
        Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("database_beta", "postrise");
        });

        Throwable cause = t.getCause();
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
        Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("not_a_database", "postrise");
        });

        Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
    }
}
