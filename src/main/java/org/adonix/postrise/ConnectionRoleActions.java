package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

interface ConnectionRoleActions {

    void setRole(Connection connection, String role, String... roles) throws SQLException;
}
