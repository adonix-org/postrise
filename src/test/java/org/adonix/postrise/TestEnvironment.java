package org.adonix.postrise;

import static java.util.Map.entry;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

abstract class TestEnvironment {

    public enum Servers {
        ALPHA,
        BETA,
        GAMMA,
        DELTA
    }

    private static Map<Servers, Supplier<Server>> SERVERS = Map.ofEntries(
            entry(Servers.ALPHA, AlphaServer::getInstance),
            entry(Servers.BETA, BetaServer::getInstance),
            entry(Servers.GAMMA, GammaServer::getInstance),
            entry(Servers.DELTA, DeltaServer::getInstance));

    static final Server getServer(final Servers server) {
        return SERVERS.get(server).get();
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
        try (final Connection connection = getServer(Servers.ALPHA).getConnection("postrise", "postrise")) {
            executeSql(connection, "beta.sql");
        }
        try (final Connection connection = getServer(Servers.ALPHA).getConnection("postrise", "postrise")) {
            executeSql(connection, "delta.sql");
        }
    }

    static void executeSql(final Connection connection, final String fileName) throws Exception {
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        connection.prepareStatement(sql).executeUpdate();
    }
}
