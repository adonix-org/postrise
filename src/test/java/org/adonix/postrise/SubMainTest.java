package org.adonix.postrise;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.servers.EdgeCaseServer;
import org.adonix.postrise.servers.PostgresDocker;
import org.adonix.postrise.servers.RestartServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class SubMainTest {

    @Test
    void testServerRestart() throws Exception {

        try (final PostgresDocker server = new RestartServer()) {

            server.startContainer();
            final DataSourceContext context = server.getDataSource("postrise");
            try (final Connection connection = context.getConnection()) {
                connection.createStatement().executeQuery("SELECT 1");
            }

            server.stopContainer();
            final Throwable t = assertThrows(PSQLException.class, context::getConnection);
            assertEquals(t.getMessage(), "An I/O error occurred while sending to the backend.");

            server.startContainer();
            try (final Connection connection = context.getConnection()) {
                assertNotNull(connection);
                connection.createStatement().executeQuery("SELECT 1");
            }

            server.stopContainer();
        }
    }

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

    @DisplayName("Invalid Pool Status Request")
    @Test
    void testInvalidPoolStatusRequest() throws SQLException {
        final Server server = new EdgeCaseServer();
        server.addListener(new DataSourceListener() {
            @Override
            public void beforeCreate(final DataSourceSettings settings) {
                final DataSourceContext context = (DataSourceContext) settings;
                final Throwable t = assertThrows(IllegalStateException.class, context::getActiveConnections);
                assertEquals(t.getMessage(), "Pool status request is invalid");
            }
        });
        assertThrows(CreateDataSourceException.class, () -> server.getConnection("database"));
        server.close();
    }
}
