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

public class TestEdgeCases {

    @Test
    void testServerRestart() throws Exception {

        try (final PostgresDocker server = new RestartServer()) {

            server.startContainer();
            final DataSourceContext context = server.getDataSource("postrise");
            try (final Connection connection = context.getConnection()) {
                connection.createStatement().executeQuery("SELECT 1");
            }

            server.stopContainer();
            assertThrows(PSQLException.class, context::getConnection);

            server.startContainer();
            try (final Connection connection = context.getConnection()) {
                assertNotNull(connection);
                connection.createStatement().executeQuery("SELECT 1");
            }

            server.stopContainer();
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
        try (final Server server = new EdgeCaseServer()) {
            assertEquals(server.getDatabaseNames().size(), 0);
        }
    }

    @DisplayName("Invalid Pool Status Request")
    @Test
    void testInvalidPoolStatusRequest() throws SQLException {
        try (final Server server = new EdgeCaseServer()) {
            server.addListener(new DataSourceListener() {
                @Override
                public void beforeCreate(final DataSourceSettings settings) {
                    final DataSourceContext context = (DataSourceContext) settings;
                    final Throwable t = assertThrows(IllegalStateException.class, context::getActiveConnections);
                    assertEquals(t.getMessage(), "Pool status request is invalid");
                }
            });
            assertThrows(CreateDataSourceException.class, () -> server.getConnection("database"));
        }
    }
}
