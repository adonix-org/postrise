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

package org.adonix.postrise.servers;

import static org.adonix.postrise.security.RoleSecurityProviders.DISABLE_ROLE_SECURITY;

import org.adonix.postrise.DataSourceContext;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.DatabaseListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostriseListener implements DatabaseListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String getDatabaseName() {
        return PostgresContainer.DB_NAME;
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setRoleSecurity(DISABLE_ROLE_SECURITY);
    }

    @Override
    public void beforeClose(final DataSourceContext context) {
        LOGGER.info("{}: Threads Awaiting Connection: {}", context, context.getThreadsAwaitingConnection());
        throw new RuntimeException("Not an error. Testing event exception propagation.");
    }
}
