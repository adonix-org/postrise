package org.adonix.postrise.security;

import java.sql.SQLException;

public class RoleSecurityException extends SQLException {

    private static final String SQL_STATE_INSECURE_ROLE = "28000";

    private static final String MESSAGE_PREFIX = "ROLE SECURITY: ";

    protected RoleSecurityException(final String message) {
        super(MESSAGE_PREFIX + message.trim(), SQL_STATE_INSECURE_ROLE);
    }
}
