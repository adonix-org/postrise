package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.adonix.postrise.security.RoleSecurityException;
import org.adonix.postrise.servers.TestDatabaseListener;
import org.adonix.postrise.servers.TestServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class MainTest extends TestEnvironment {

    private static final Server server = getServerInstance(TestServer.class);

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

    @DisplayName("Max Pool Size = 1 and Check Connection Role")
    @Test
    void t19() throws SQLException {

        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        // Set the max pool size to 1. Only one connection in the pool.
        final DataSourceContext context = server.getDataSource(listener.getDatabaseName());
        context.setMaxPoolSize(1);
        assertEquals(context.getMaxPoolSize(), 1);

        // Set the single connection in the pool to the beta_application role.
        try (final Connection connection = context.getConnection("no_login_no_super")) {
            assertNotNull(connection);

            // Set the single connection auto commit to false to verify reset.
            // TODO: Make this a separate test.
            connection.setAutoCommit(false);
        }

        // Get the single connection and verify the current_user has reverted to
        // the postrise role and auto commit reverted to true.
        try (final Connection connection = context.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT session_user, current_user");
                ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("with_login_no_super", rs.getString(1));
            assertEquals("with_login_no_super", rs.getString(2));
            assertEquals(connection.getAutoCommit(), true);
        }
    }
}
