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

    private static Map<String, Supplier<Server>> SINGLETONS = Map.ofEntries(
            getEntry(AlphaServer::new),
            getEntry(BetaServer::new),
            getEntry(GammaServer::new),
            getEntry(DeltaServer::new));

    private static final Entry<String, Supplier<Server>> getEntry(final Supplier<Server> supplier) {
        return entry(getKey(supplier), supplier);
    }

    private static final String getKey(final Class<? extends Server> clazz) {
        return clazz.getSimpleName();
    }

    private static final String getKey(final Supplier<Server> supplier) {
        return getKey(supplier.get().getClass());
    }

    protected static final Server getServer(final Class<? extends Server> clazz) {
        return SINGLETONS.get(getKey(clazz)).get();
    }

    @BeforeAll
    static final void beforeAll() throws Exception {
        PostgresTestServer.start();
        initialze();
    }

    @AfterAll
    static final void afterAll() throws Exception {
        for (final Supplier<Server> server : SINGLETONS.values()) {
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
