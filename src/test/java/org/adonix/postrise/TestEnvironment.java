package org.adonix.postrise;

import static java.util.Map.entry;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

abstract class TestEnvironment {

    private static Map<String, Server> instances = Map.ofEntries(
            getEntry(new AlphaServer()),
            getEntry(new BetaServer()),
            getEntry(new GammaServer()),
            getEntry(new DeltaServer()));

    private static final Entry<String, Server> getEntry(final Server server) {
        return entry(getKey(server.getClass()), server);
    }

    private static final String getKey(final Class<? extends Server> clazz) {
        return clazz.getName();
    }

    static final Server getServerInstance(final Class<? extends Server> clazz) {
        return instances.get(getKey(clazz));
    }

    @BeforeAll
    static final void beforeAll() throws Exception {
        PostgresTestServer.start();
        initialize();
    }

    @AfterAll
    static final void afterAll() throws Exception {
        for (final Server server : instances.values()) {
            server.close();
        }
        PostgresTestServer.stop();
    }

    static void initialize() throws Exception {
        try (final Connection connection = getServerInstance(AlphaServer.class).getConnection("postrise", "postrise")) {
            executeSql(connection, "beta.sql");
        }
        try (final Connection connection = getServerInstance(AlphaServer.class).getConnection("postrise", "postrise")) {
            executeSql(connection, "delta.sql");
        }
    }

    static void executeSql(final Connection connection, final String fileName) throws Exception {
        connection.setAutoCommit(true);
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        connection.prepareStatement(sql).executeUpdate();
    }
}
