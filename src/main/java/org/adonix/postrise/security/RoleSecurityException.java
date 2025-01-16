package org.adonix.postrise.security;

import java.sql.SQLException;

public class RoleSecurityException extends SQLException {

    private static final String MESSAGE_PREFIX = "SECURITY: ";

    protected RoleSecurityException(final String message) {
        super(MESSAGE_PREFIX + message.trim());
    }
}
