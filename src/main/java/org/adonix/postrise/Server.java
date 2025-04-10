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

    /**
     * Returns the host name or IP address of the server.
     *
     * @return the host name of the server, not {@code null}.
     */
    String getHostName();

    /**
     * Returns the port number used to connect to the server.
     *
     * @return the port number.
     */
    Integer getPort();

    /**
     * Add a {@link DataSourceListener} to the server. When an event is dispatched
     * by the {@link Server}, the data source listeners will be notified in the
     * order they were added. Then if a {@link DatabaseListener} exists for a given
     * database, that object will be notified.
     * 
     * @param listener an object to be notified
     */
    void addListener(DataSourceListener listener);

    /**
     * Add a {@link DatabaseListener} to the server. When an event is dispatched
     * by the {@link Server}, the data source listeners will be notified in the
     * order they were added. Then if a {@link DatabaseListener} exists for a given
     * database, that object will be notified.
     * 
     * @param listener an object to be notified
     */
    void addListener(DatabaseListener listener);

    /**
     * Get a {@link Connection} from the {@link Server}.
     * 
     * @param databaseName - the name of the target database.
     * @return a live {@link Connection} to the specified database.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection(String databaseName) throws SQLException;

    /**
     * Get a {@link Connection} from the {@link Server} with the specified
     * {@code ROLE}.
     * 
     * @param databaseName - the name of the target database.
     * @param roleName     - the name of the {@code ROLE} the {@link Connection}
     *                     will use.
     * @return a live {@link Connection} to the specified database.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection(String databaseName, String roleName) throws SQLException;

    /**
     * Get the {@link DataSourceContext} specified by the database name.
     * 
     * @param databaseName - the name of the target database.
     * @return a live {@link DataSourceContext} for the target database.
     */
    DataSourceContext getDataSource(String databaseName);

    /**
     * The set of database names for which live data sources currently exist on this
     * {@link Server}.
     * 
     * @return the set of database names associated with active data sources.
     */
    Set<String> getDatabaseNames();

    /**
     * Get the cumulative number of connections currently in all pools. The return
     * value is transient and is a point-in-time measurement.
     *
     * @return the total number of connections in all pools.
     */
    int getTotalConnections();

    /**
     * Get the cumulative number of currently idle connections in all pools. The
     * return value is extremely transient and is a point-in-time measurement.
     *
     * @return the current number of idle connections in all pools.
     */
    int getIdleConnections();

    /**
     * Get the cumulative number of currently active connections in all pools. The
     * return value is extremely transient and is a point-in-time measurement.
     *
     * @return the current number of active (in-use) connections in all pools.
     */
    int getActiveConnections();

    /**
     * Get the cumulative number of threads awaiting connections from all pools. The
     * return value is extremely transient and is a point-in-time measurement.
     *
     * @return the number of threads awaiting a connection from all pools
     */
    int getThreadsAwaitingConnection();

    /**
     * Close the server and all data sources contained within.
     * <p>
     * Subsequent calls after the first successful close will have no effect.
     */
    void close();
}
