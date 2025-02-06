package org.adonix.postrise.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.adonix.postrise.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostriseFireThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(PostriseFireThread.class);

    private static final Server server = PostriseFireServer.getInstance();
    private static final String SQL_STRING = "SELECT 1";

    @Override
    public void run() {
        try (final Connection connection = server.getConnection("postrise", "catch_fire");
                final PreparedStatement statement = connection.prepareStatement(SQL_STRING);
                final ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                LOGGER.info("{} rs.getString() - {}", threadId(), rs.getString(1));
            }
        } catch (final Exception e) {
            LOGGER.error(e);
        }
    }
}
