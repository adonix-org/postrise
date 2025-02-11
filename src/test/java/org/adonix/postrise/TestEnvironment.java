package org.adonix.postrise;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import org.adonix.postrise.servers.PostgresDocker;

abstract class TestEnvironment {

    public static void initialize(final Server server) throws Exception {
        try (final Connection connection = server.getConnection(PostgresDocker.DB_NAME)) {
            executeSqlFile(connection, "roles.sql");
        }
    }

    static void executeSqlFile(final Connection connection, final String fileName) throws Exception {
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
