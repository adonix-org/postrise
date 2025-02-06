package org.adonix.postrise.performance;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import org.adonix.postrise.Server;
import org.adonix.postrise.servers.PostgresDocker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Performance {

    @AutoClose
    final static Server server = PostriseFireServer.getInstance();

    @Test
    public void run() throws Exception {
        try (final Connection connection = server.getConnection("postrise", "fire")) {
            assertNotNull(connection);
        }
    }

    @BeforeAll
    static void beforeAll() {
        PostgresDocker.start();
    }

    @AfterAll
    static void afterAll() {
        PostgresDocker.stop();
    }
}
