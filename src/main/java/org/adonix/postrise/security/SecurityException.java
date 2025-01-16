package org.adonix.postrise.security;

import java.sql.SQLException;

public final class SecurityException extends SQLException {

    private static final String MESSAGE_PREFIX = "SECURITY: ";

    protected SecurityException(final String message) {
        super(MESSAGE_PREFIX + message.trim());
    }
}
