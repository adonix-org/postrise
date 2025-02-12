package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.adonix.postrise.servers.EdgeCaseServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SubMainTest {
    @DisplayName("Postgres Server Validate Default Host and Port")
    @Test
    void testPostgresServerDefaultHostPort() throws SQLException {
        try (final Server server = new PostgresServer()) {
            assertEquals(server.getHostName(), PostgresServer.POSTGRES_DEFAULT_HOSTNAME);
            assertEquals(server.getPort(), PostgresServer.POSTGRES_DEFAULT_PORT);
        }
    }

    @DisplayName("Server Close Idempotency")
    @Test
    void testServerCloseIdempotency() throws SQLException {
        final Server server = new PostgresServer();
        server.close();
        server.close();
        server.close();
    }

    @DisplayName("Server Edge Cases")
    @Test
    void testServerEdgeCases() throws SQLException {
        final Server server = new EdgeCaseServer();
        assertEquals(server.getDatabaseNames().size(), 0);
        server.close();
    }
}
