package org.adonix.postrise;

import static java.util.Map.entry;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

abstract class TestEnvironment {

    private static Map<String, Supplier<Server>> SERVERS = Map.ofEntries(
            getEntry(AlphaServer::getInstance),
            getEntry(BetaServer::getInstance),
            getEntry(GammaServer::getInstance),
            getEntry(DeltaServer::getInstance));

    private static final Entry<String, Supplier<Server>> getEntry(final Supplier<Server> supplier) {
        return entry(getKey(supplier.get().getClass()), supplier);
    }

    private static final String getKey(final Class<? extends Server> clazz) {
        return clazz.getSimpleName();
    }

    protected static final Server getServer(final Class<? extends Server> clazz) {
        return SERVERS.get(getKey(clazz)).get();
    }

    @BeforeAll
    static final void beforeAll() throws Exception {
        PostgresTestServer.start();
        initialze();
    }

    @AfterAll
    static final void afterAll() throws Exception {
        for (final Supplier<Server> server : SERVERS.values()) {
            server.get().close();
        }
        PostgresTestServer.stop();
    }

    static void initialze() throws Exception {
        try (final Connection connection = getServer(AlphaServer.class).getConnection("postrise", "postrise")) {
            executeSql(connection, "beta.sql");
        }
        try (final Connection connection = getServer(AlphaServer.class).getConnection("postrise", "postrise")) {
            executeSql(connection, "delta.sql");
        }
    }

    static void executeSql(final Connection connection, final String fileName) throws Exception {
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        connection.prepareStatement(sql).executeUpdate();
    }
}
