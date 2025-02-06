package org.adonix.postrise.performance;

import org.adonix.postrise.Server;
import org.adonix.postrise.servers.PostgresDocker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Performance {

    @AutoClose
    final static Server server = PostriseFireServer.getInstance();

    @BeforeAll
    static void beforeAll() {
        PostgresDocker.start();
    }

    @Test
    public void run() throws Exception {
        server.getConnection("postrise", "fire");
    }

    @AfterAll
    static void afterAll() {
        PostgresDocker.stop();
    }
}
