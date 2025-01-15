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

/**
 * A no-op implementation of {@link SecurityEventListener} that disables all
 * security checks for user login and role validation.
 * 
 * <p>
 * This class is for scenarios where security checks are unnecessary, such
 * as when performing super-user DDL operations like creating databases,
 * indexes, tables, or roles.
 * </p>
 */
final class DisableSecurity implements SecurityEventListener {

    /**
     * Constructs a new {@code DisableSecurity} instance.
     * <p>
     * Constructor is package-private.
     */
    protected DisableSecurity() {
        // No initialization required for this implementation.
    }

    /**
     * Disables the user login security check. This implementation does nothing.
     *
     * @param connection the database connection (ignored).
     * @param user       the user attempting to log in (ignored).
     */
    @Override
    public void onLogin(final Connection connection, final String user) {
        // No action taken; user login security checks are disabled.
    }

    /**
     * Disables the role validation security check. This implementation does
     * nothing.
     *
     * @param connection the database connection (ignored).
     * @param role       the role to be validated (ignored).
     */
    @Override
    public void onConnection(final Connection connection, final String role) {
        // No action taken; role security checks are disabled.
    }
}
