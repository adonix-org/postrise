package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
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
        try (final Connection connection = BETA.getConnection("beta_app", "beta_application")) {
            connection.getMetaData();
        }
    }

    @DisplayName("Super User Security Exception")
    @Test
    void run3() {
        Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            GAMMA.getConnection("beta_app", "postrise");
        });

        Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof SQLException);
        assertEquals("SECURITY: role 'postrise' is a super user", cause.getMessage());
    }

    @DisplayName("Postgres Exception Propagation")
    @Test
    void run4() {
        Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            GAMMA.getConnection("no_database", "postrise");
        });

        Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"no_database\" does not exist", cause.getMessage());
    }
}
