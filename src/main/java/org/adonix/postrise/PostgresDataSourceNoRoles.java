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

import java.sql.Connection;

/**
 * If PostgreSQL roles are not being used by the application, performance will
 * be improved by using this data source.
 * <p>
 * The increase in performance is due to not having to {@code RESET ROLE} when
 * acquiring each {@link Connection}.
 */
public final class PostgresDataSourceNoRoles extends PostgresDataSource {

    /**
     * Instances are created by {@link PostgresServer#createDataSource(String)}
     * 
     * @param server       - the parent of this data source.
     * @param databaseName - name of the PostgreSQL database (case-sensitive).
     */
    public PostgresDataSourceNoRoles(final Server server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public Connection getConnection(final String roleName) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support roles");
    }
}
