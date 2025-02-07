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

package org.adonix.postrise;

import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_DEFAULT_ROLE_SECURITY;

import org.adonix.postrise.security.RoleSecurityListener;

public class PostgresDataSource extends PostriseSecureDataSource {

    protected static final String POSTGRES_URL_PREFIX = "jdbc:postgresql://";

    protected PostgresDataSource(final String databaseName) {
        super(databaseName);
        addDataSourceProperty("tcpKeepAlive", "true");
    }

    @Override
    public final void setJdbcUrl(final String host, final Integer port) {
        setJdbcUrl(POSTGRES_URL_PREFIX + host + ":" + port + "/" + getDatabaseName());
    }

    @Override
    public RoleSecurityListener getDefaultRoleSecurity() {
        return POSTGRES_DEFAULT_ROLE_SECURITY;
    }
}
