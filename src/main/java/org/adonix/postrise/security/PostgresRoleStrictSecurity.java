package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

final class PostgresRoleStrictSecurity extends PostgresRoleDefaultSecurity {

    PostgresRoleStrictSecurity() {
        super();
    }

    /**
     * This role used for requesting the {@link Connection} should not have LOGIN or
     * SUPER user privileges.
     * <p>
     * {@inheritDoc}
     * 
     * @throws RoleSecurityException if the role is a LOGIN role, or is a SUPER
     *                               user.
     * 
     */
    @Override
    public void onConnection(final Connection connection, final String roleName) throws SQLException {
        final PostgresRole role = PostgresRoleDAO.getRole(connection, roleName);
        if (role.isSuperUser()) {
            throw new RoleSecurityException("role '" + role.getRoleName() + "' is a super user");
        }
        if (role.isLoginRole()) {
            throw new RoleSecurityException("role '" + role.getRoleName() + "' is a login user");
        }
    }
}
