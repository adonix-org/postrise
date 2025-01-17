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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.adonix.postrise.security.SecurityEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements ConfigurationListener, Server {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Subclasses will create and return a new instance of a
     * {@link ConnectionProvider} implementation.
     * 
     * @param database - the database for the new {@link ConnectionProvider}.
     * @return a new {@link ConnectionProvider}.
     * @see PostriseDataSource
     */
    protected abstract ConnectionProvider createConnectionProvider(final String database);

    protected abstract SecurityEventListener getSecurityProvider();

    protected abstract void setRole(final Connection connection, final String roleName) throws SQLException;

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    private final Set<ConfigurationListener> configurationListeners = Collections
            .synchronizedSet(new LinkedHashSet<>());

    private final Map<String, DatabaseListener> databaseListeners = new ConcurrentHashMap<>();

    public PostriseServer() {
        addListener(this);
    }

    public final void addListener(final ConfigurationListener listener) {
        Guard.check("listener", listener);
        configurationListeners.add(listener);
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        if (databaseListeners.put(getKey(listener), listener) != null) {
            LOGGER.warn("Overwriting existing configuration for database '{}'", listener.getDatabaseName());
        }
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
    }

    @Override
    public final Connection getConnection(final String database, final String roleName) throws SQLException {

        Guard.check("database", database);
        Guard.check("roleName", roleName);

        final ConnectionProvider provider = databasePools.computeIfAbsent(getKey(database), _ -> create(database));

        final Connection connection = provider.getConnection();
        try {
            // Security check on the role.
            getSecurityProvider().onConnection(connection, roleName);

            // Security check passed, set role on the connection.
            setRole(connection, roleName);

            return connection;

        } catch (final SQLException e) {
            connection.close();
            throw e;
        }
    }

    /**
     * 
     * @param database - the name of the database for creating the
     *                 {@link ConnectionProvider}.
     * @return a valid and configured {@link ConnectionProvider} implementation.
     */
    private final ConnectionProvider create(final String database) {

        final ConnectionProvider connectionProvider = createConnectionProvider(database);

        // Set the default JDBC Url for this provider.
        connectionProvider.setJdbcUrl(getHostName(), getPort());

        for (final ConfigurationListener listener : configurationListeners) {
            listener.onConfigure(connectionProvider);
        }

        final DatabaseListener listener = databaseListeners.get(getKey(database));
        if (listener != null) {
            listener.onConfigure(connectionProvider);
        }

        // Create the first connection to validate settings, initialize the connection
        // pool, and validate security for login user.
        try (final Connection connection = connectionProvider.getConnection()) {

            getSecurityProvider().onLogin(connection, connectionProvider.getUsername());
            return connectionProvider;

        } catch (final SQLException e) {
            connectionProvider.close();
            throw new CreateDataSourceException(connectionProvider.getJdbcUrl(), e);
        }
    }

    @Override
    public final synchronized void close() {
        try {
            for (final ConnectionProvider provider : databasePools.values()) {
                LOGGER.info("Closing {}@{} for {}", provider.getUsername(), provider.getJdbcUrl(),
                        this.getClass().getSimpleName());
                try {
                    provider.close();
                } catch (final Exception e) {
                    LOGGER.error("Closing {}@{}", provider.getUsername(), provider.getJdbcUrl(), e);
                }
            }
        } finally {
            databasePools.clear();
        }
    }

    private static final String getKey(final String database) {
        return database.trim();
    }

    private static final String getKey(final DatabaseListener listener) {
        return getKey(listener.getDatabaseName());
    }

    @Override
    public String toString() {
        return getHostName() + ":" + getPort();
    }
}
