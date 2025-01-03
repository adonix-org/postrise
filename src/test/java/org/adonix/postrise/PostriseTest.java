package org.adonix.postrise;

import java.sql.Connection;
import org.adonix.postrise.servers.Servers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class PostriseTest extends TestEnvironment {

    private static final Logger LOGGER = LogManager.getLogger();

    @DisplayName("Run")
    @Test
    public void run() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run");
        }
    }

    @DisplayName("Run2")
    @Test
    public void run2() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run2");
        }
    }

    @DisplayName("Run3")
    @Test
    public void run3() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run3");
        }
    }
}
