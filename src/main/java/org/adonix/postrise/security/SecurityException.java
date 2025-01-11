package org.adonix.postrise.security;

import java.sql.SQLException;

class SecurityException extends SQLException {

    private static final String EXCEPTION_PREFIX = "SECURITY: ";

    public SecurityException(final String message) {
        super(EXCEPTION_PREFIX + message.trim());
    }
}
