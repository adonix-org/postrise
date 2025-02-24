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
