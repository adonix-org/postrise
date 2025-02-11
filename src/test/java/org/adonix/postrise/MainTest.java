package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.adonix.postrise.security.RoleSecurityException;
import org.adonix.postrise.servers.TestDatabaseListener;
import org.adonix.postrise.servers.TestServer;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class MainTest extends TestEnvironment {

    @AutoClose
    private static final Server server = new TestServer();

    @DisplayName("SUPERUSER Security Exception")
    @Test
    void t03() throws SQLException {

        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: with_login_with_super is a SUPER user", cause.getMessage());
    }

    /**
     * This test validates that {@link PSQLException} errors are propagated when
     * they occur.
     */
    @DisplayName("Postgres Exception Propagation")
    @Test
    void t04() {
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("not_a_database");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
    }
}
