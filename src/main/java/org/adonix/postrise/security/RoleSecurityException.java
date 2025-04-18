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

import java.sql.SQLException;

/**
 * Thrown for any {@code ROLE} security exceptions.
 */
public final class RoleSecurityException extends SQLException {

    private static final long serialVersionUID = 1L;

	/**
     * The SQL State "invalid_role_specification" passed to the super-class
     * {@link SQLException}.
     */
    private static final String INVALID_ROLE = "0P000";

    /**
     * {@code ROLE} exception messages will share the SECURITY prefix.
     */
    private static final String MESSAGE_PREFIX = "SECURITY: ";

    /**
     * Create a {@code RoleSecurityException}.
     * 
     * @param message - the {@code ROLE} security error message.
     */
    public RoleSecurityException(final String message) {
        super(MESSAGE_PREFIX + message.trim(), INVALID_ROLE);
    }
}
