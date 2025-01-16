package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.security.RoleSecurityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class TestCases extends TestEnvironment {

    @Test
    void run1() throws SQLException {
        try (final Connection connection = ALPHA.getConnection("postrise", "postrise")) {
            connection.getMetaData();
        }
    }

    @Test
    void run2() throws SQLException {
        try (final Connection connection = DELTA.getConnection("database_delta", "delta_application")) {
            connection.getMetaData();
        }
    }

    @DisplayName("Super User Security Exception")
    @Test
    void run3() {
        Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            GAMMA.getConnection("database_beta", "postrise");
        });

        Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: role 'postrise' is a super user", cause.getMessage());
    }

    @DisplayName("Postgres Exception Propagation")
    @Test
    void run4() {
        Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            ALPHA.getConnection("not_a_database", "postrise");
        });

        Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
    }
}
