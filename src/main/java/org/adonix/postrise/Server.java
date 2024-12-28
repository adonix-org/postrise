package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

public interface Server extends AutoCloseable {

    String getHostname();

    int getPort();

    Connection getConnection(String database, String role) throws SQLException;
}
