package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EventListener;

public interface UserSecurityListener extends EventListener {

    void onLogin(Connection connection, String user) throws SQLException;

    void onConnection(Connection connection, String role) throws SQLException;
}
