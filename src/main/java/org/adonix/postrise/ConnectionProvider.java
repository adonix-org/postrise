package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

interface ConnectionProvider extends ConnectionSettings {

    Connection getConnection() throws SQLException;
}
