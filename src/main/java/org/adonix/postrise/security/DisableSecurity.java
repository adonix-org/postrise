package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DisableSecurity implements SecurityProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    public DisableSecurity() {
        LOGGER.warn("Security is disabled");
    }

    @Override
    public void onLogin(final Connection connection, final String user) throws SQLException {
        // Disable user login security checks.
    }

    @Override
    public void onConnection(final Connection connection, final String role) throws SQLException {
        // Disable role security checks.
    }
}
