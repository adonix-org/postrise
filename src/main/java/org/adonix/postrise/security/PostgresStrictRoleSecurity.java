package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.DataSourceContext;

final class PostgresStrictRoleSecurity extends PostgresDefaultRoleSecurity {

    PostgresStrictRoleSecurity() {
    }

    @Override
    public void onSetRole(DataSourceContext context, Connection connection, String roleName) throws SQLException {
        final PostgresRole role = PostgresRoleDAO.getRole(connection, roleName);
        if (role.isSuperUser()) {
            throw new RoleSecurityException("\"" + role.getRoleName() + "\" is a SUPERUSER role");
        }
        if (role.isLoginRole()) {
            throw new RoleSecurityException("\"" + role.getRoleName() + "\" is a LOGIN role");
        }
    }
}
