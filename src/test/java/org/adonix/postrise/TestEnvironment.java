package org.adonix.postrise;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

abstract class TestEnvironment {

    static final Server ALPHA = AlphaServer.getInstance();
    static final Server BETA = BetaServer.getInstance();

    @BeforeAll
    static final void beforeAll() throws Exception {
        PostgresContainer.start();
        initialze();
    }

    @AfterAll
    static final void afterAll() throws Exception {
        ALPHA.close();
        BETA.close();
        PostgresContainer.stop();
    }

    static void initialze() throws Exception {
        try (final Connection connection = ALPHA.getConnection("postrise", "postrise")) {
            executeSql(connection, "initialize.sql");
        }
        try (final Connection connection = ALPHA.getConnection("beta_app", "postrise")) {
            executeSql(connection, "adonix.sql");
        }
    }

    static void executeSql(final Connection connection, final String fileName) throws Exception {
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        connection.prepareStatement(sql).executeUpdate();
    }
}
