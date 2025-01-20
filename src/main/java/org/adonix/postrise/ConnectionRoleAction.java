package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

interface ConnectionRoleAction {

    void setRole(Connection connection, String role, String... roles) throws SQLException;
}
