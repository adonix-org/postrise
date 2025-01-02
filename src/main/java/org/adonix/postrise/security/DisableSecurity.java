/*
 * Copyright (C) 2024, 2025 Ty Busby
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DisableSecurity implements SecurityEventListener {

    private static final Logger LOGGER = LogManager.getLogger();

    public DisableSecurity() {
        LOGGER.warn("Security is disabled");
    }

    @Override
    public void onLogin(final Connection connection, final String user) {
        // No user login security check.
    }

    @Override
    public void onConnection(Connection connection, String role) throws SQLException {
        // No role security check.
    }
}
