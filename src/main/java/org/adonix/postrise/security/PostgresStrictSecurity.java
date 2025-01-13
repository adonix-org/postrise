package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

final class PostgresStrictSecurity extends PostgresDefaultSecurity {

    protected PostgresStrictSecurity() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SecurityException if the role does not exist, is a login role, or is
     *                           a super user.
     */
    @Override
    public void onConnection(Connection connection, String roleName) throws SQLException {
        final PostgresRole role = PostgresRoleDAO.getRole(connection, roleName);
        if (role.isSuperUser()) {
            throw new SecurityException("user '" + role.getRoleName() + "' is a super user");
        }
        if (!role.isLoginUser()) {
            throw new SecurityException("user '" + role.getRoleName() + "' is a login user");
        }
    }
}
