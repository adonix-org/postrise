package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

interface ConnectionRole {
    
    void setRole(Connection connection, String... roles) throws SQLException;
}
