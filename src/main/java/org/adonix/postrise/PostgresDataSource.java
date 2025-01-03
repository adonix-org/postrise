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

class PostgresDataSource extends PostriseDataSource {

    private static final String POSTGRES_URL_PREFIX = "jdbc:postgresql://";

    public PostgresDataSource(final Server server, final String database) {
        super(database);
        setJdbcUrl(getJdbcUrl(server));
        addDataSourceProperty("tcpKeepAlive", "true");
    }

    public PostgresDataSource(final String host, final Integer port, final String database) {
        super(database);
        setJdbcUrl(getJdbcUrl(host, port));
        addDataSourceProperty("tcpKeepAlive", "true");
    }

    @Override
    public String getJdbcUrl(final String host, final Integer port) {
        return POSTGRES_URL_PREFIX + host + ":" + port + "/" + getDatabase();
    }
}
