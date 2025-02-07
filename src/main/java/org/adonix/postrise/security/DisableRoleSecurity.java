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

/**
 * A no-op implementation of {@link RoleSecurityListener} that disables all
 * security checks for user login and role validation.
 * 
 * <p>
 * This class is for scenarios where security checks are unnecessary, such
 * as when performing super-user DDL operations like creating databases,
 * indexes, tables, or roles.
 * </p>
 */
class DisableRoleSecurity implements RoleSecurityListener {

    /**
     * Constructs a new {@code DisableRoleSecurity} instance.
     * <p>
     * Constructor is <code>package-private</code>.
     */
    DisableRoleSecurity() {
    }

    @Override
    public void onLogin(final DataSourceContext context, final Connection connection) throws SQLException {
        // Security is disabled for this event.
    }
}
