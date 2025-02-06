package org.adonix.postrise.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostriseThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(PostriseThread.class);

    private static final String SQL_STRING = "SELECT 1";

    @Override
    public void run() {
        try (final Connection connection = PostriseFireServer.getConnection("fire");
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
