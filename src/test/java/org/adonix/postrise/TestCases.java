package org.adonix.postrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.adonix.postrise.servers.AlphaServer;
import org.adonix.postrise.servers.GammaServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestCases extends TestEnvironment {

    private PostriseServer getServerInstance(Class<? extends Server> class1) {
        return null;
    }

    @DisplayName("PostriseDataSource Getters and Setters at Runtime")
    @Test
    void t14() {
        final PostriseServer server = getServerInstance(AlphaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof AlphaServer);

        final DataSourceContext dataSource = server.getDataSource("postrise");
        assertNotNull(dataSource);

        dataSource.setMaxPoolSize(50);
        assertEquals(50, dataSource.getMaxPoolSize());

        dataSource.setMinIdle(1);
        assertEquals(1, dataSource.getMinIdle());

        assertTrue(dataSource.isAutoCommit());
    }

    @DisplayName("Connection Limit = 1 for beta_login User")
    @Test
    void t20() throws SQLException, InterruptedException {
        final Server server = getServerInstance(GammaServer.class);
        assertNotNull(server);
        assertTrue(server instanceof GammaServer);

        final DataSourceContext context = server.getDataSource("database_beta");
        assertEquals(context.getMaxPoolSize(), 10);
        assertEquals(context.getMinIdle(), 10);

        assertEquals(context.getTotalConnections(), 1);
    }
}
