package org.adonix.postrise.security;

import java.sql.SQLException;

public class SecurityException extends SQLException {

    public SecurityException(String message) {
        super(message);
    }
}
