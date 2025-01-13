package org.adonix.postrise.security;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresRole {

    private final String roleName;
    private final Boolean isSuperUser;
    private final Boolean isLoginUser;
    private final Boolean isInherit;
    private final Boolean isCreateRole;
    private final Boolean isCreateDbRole;
    private final Boolean isReplicationRole;

    protected PostgresRole(final ResultSet resultSet) throws SQLException {
        roleName = resultSet.getString(1);
        isSuperUser = resultSet.getBoolean(2);
        isLoginUser = resultSet.getBoolean(3);
        isInherit = resultSet.getBoolean(4);
        isCreateRole = resultSet.getBoolean(5);
        isCreateDbRole = resultSet.getBoolean(6);
        isReplicationRole = resultSet.getBoolean(7);
    }

    public String getRoleName() {
        return roleName;
    }

    public Boolean isSuperUser() {
        return isSuperUser;
    }

    public Boolean isLoginRole() {
        return isLoginUser;
    }

    public Boolean isInheritRole() {
        return isInherit;
    }

    public Boolean isCreateRole() {
        return isCreateRole;
    }

    public Boolean isCreateDbRole() {
        return isCreateDbRole;
    }

    public Boolean isReplicationRole() {
        return isReplicationRole;
    }
}
