/*
 * Copyright (C) 2025 Ty Busby
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.DataSourceContext;
import org.adonix.postrise.DataSourceSettings;

/**
 * The {@code DefaultSecurity} class provides default security checks
 * for user logins and connections in a database environment.
 * <p>
 * It implements the {@link RoleSecurityListener} interface, performing
 * specific validation for users and roles using SQL queries on the
 * PostgreSQL system catalog.
 * </p>
 */
class PostgresDefaultRoleSecurity implements RoleSecurityListener {

    /**
     * Constructs a new {@code DefaultSecurity} instance.
     */
    PostgresDefaultRoleSecurity() {
    }

    /**
     * {@inheritDoc}
     * 
     * @throws RoleSecurityException is not a login user, or is a super user.
     */
    @Override
    public void onLogin(final DataSourceSettings settings, final Connection connection) throws SQLException {
        final PostgresRole role = PostgresRoleDAO.getRole(connection, settings.getLoginRole());
        if (role.isSuperUser()) {
            throw new RoleSecurityException(role.getRoleName() + " is a super user");
        }
        if (!role.isLoginRole()) {
            throw new RoleSecurityException(role.getRoleName() + " is not a login user");
        }
    }

    @Override
    public void onConnection(final DataSourceContext context, final Connection connection, final String roleName)
            throws SQLException {
        if (context.getLoginRole().equals(roleName)) {
            throw new RoleSecurityException(roleName + " is a LOGIN role");
        }
    }
}
