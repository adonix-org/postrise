package org.adonix.postrise;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

abstract class TestEnvironment {

    private static List<Supplier<Server>> servers = new LinkedList<>();

    static final Server ALPHA = getServer(AlphaServer::getInstance);
    static final Server BETA = getServer(BetaServer::getInstance);
    static final Server GAMMA = getServer(GammaServer::getInstance);
    static final Server DELTA = getServer(DeltaServer::getInstance);

    static Server getServer(Supplier<Server> supplier) {
        servers.add(supplier);
        return supplier.get();
    }

    @BeforeAll
    static final void beforeAll() throws Exception {
        PostgresTestServer.start();
        initialze();
    }

    @AfterAll
    static final void afterAll() throws Exception {
        for (final Supplier<Server> server : servers) {
            server.get().close();
        }
        PostgresTestServer.stop();
    }

    static void initialze() throws Exception {
        try (final Connection connection = ALPHA.getConnection("postrise", "postrise")) {
            executeSql(connection, "beta.sql");
        }
        try (final Connection connection = ALPHA.getConnection("postrise", "postrise")) {
            executeSql(connection, "delta.sql");
        }
    }

    static void executeSql(final Connection connection, final String fileName) throws Exception {
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        connection.prepareStatement(sql).executeUpdate();
    }
}
