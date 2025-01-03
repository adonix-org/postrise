package org.adonix.postrise;

import java.sql.Connection;

import org.adonix.postrise.servers.Servers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class PostriseTest2 extends TestEnvironment {

    private static final Logger LOGGER = LogManager.getLogger();

    @Test
    public void run4() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run4");
        }
    }

    @Test
    public void run5() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run5");
        }
    }

    @Test
    public void run6() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run6");
        }
    }
}
