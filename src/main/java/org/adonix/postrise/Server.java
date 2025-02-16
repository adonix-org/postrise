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
import java.sql.SQLException;
import java.util.Set;

public interface Server extends AutoCloseable {

    String getHostName();

    Integer getPort();

    void addListener(DataSourceListener listener);

    void addListener(DatabaseListener listener);

    Connection getConnection(String databaseName) throws SQLException;

    Connection getConnection(String databaseName, String roleName) throws SQLException;

    DataSourceContext getDataSource(String databaseName);

    Set<String> getDatabaseNames();

    int getTotalConnections();

    int getIdleConnections();

    int getActiveConnections();

    int getThreadsAwaitingConnection();

    void close();
}
