package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DisableUserSecurity implements UserSecurityListener {

    private static final Logger LOGGER = LogManager.getLogger();

    public DisableUserSecurity() {
        LOGGER.warn("User security is disabled");
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
