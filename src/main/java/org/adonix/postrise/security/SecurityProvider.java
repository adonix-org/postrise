package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

public interface SecurityProvider {

    void onLogin(Connection connection, String user) throws SQLException;

    void onConnection(Connection connection, String role) throws SQLException;
}
