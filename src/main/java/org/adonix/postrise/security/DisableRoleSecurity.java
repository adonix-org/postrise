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
import org.adonix.postrise.DataSourceContext;

/**
 * This implementation of {@link RoleSecurityListener} is no-op for all security
 * events.
 * <p>
 * Use when SUPERUSER privileges are required.
 */
final class DisableRoleSecurity implements RoleSecurityListener {

    /**
     * Constructs a new package-private {@code DisableRoleSecurity} instance.
     * <p>
     * The {@code static} instance is created and accessed via
     * {@link RoleSecurityProvider}.
     */
    DisableRoleSecurity() {
    }

    /**
     * Disables security checks for the
     * {@link RoleSecurityListener#onLogin(DataSourceContext, Connection) onLogin}
     * event.
     * <p>
     * This implementation is no-op.
     * 
     * @param context    - ignored
     * @param connection - ignored
     */
    @Override
    public void onLogin(final DataSourceContext context, final Connection connection) {
        // Security is disabled for this event.
    }
}
