package org.adonix.postrise.servers.localhost;

import org.adonix.postrise.PostgresServer;
import org.adonix.postrise.PostgresTestConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class Alpha extends PostgresServer {

    private static final PostgresTestConfiguration configuration = new PostgresTestConfiguration();

    public Alpha() {
        super();
        addListener(configuration);
    }

    @BeforeAll
    public static void beforeAll() throws Exception {
        PostgresTestConfiguration.getContainer().start();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        PostgresTestConfiguration.getContainer().stop();
    }
}
