package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class RoleSecurity implements SecurityEventListener {

    protected abstract void setRole(Connection connection, String role) throws SQLException;

    @Override
    public void onConnection(Connection connection, String role) throws SQLException {
        setRole(connection, role);
    }
}
