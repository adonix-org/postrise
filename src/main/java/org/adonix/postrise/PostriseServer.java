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

public abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger();

    protected abstract ConnectionProvider getDataSource(final String database);

    protected abstract void setRole(final Connection connection, final String role) throws SQLException;

    protected abstract SecurityEventListener getSecurityProvider();

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    private final Set<DataSourceListener> dataSourceListeners = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Map<String, DatabaseListener> dataBaseListeners = new ConcurrentHashMap<>();

    public PostriseServer() {
        addListener(this);
    }

    public final void addListener(final DataSourceListener listener) {
        Guard.check("listener", listener);
        dataSourceListeners.add(listener);
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        if (dataBaseListeners.put(getKey(listener), listener) != null) {
            LOGGER.warn("Overwriting existing configuration for database '{}'", listener.getDatabaseName());
        }
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
    }

    @Override
    public final Connection getConnection(final String database, final String role) throws SQLException {

        Guard.check("database", database);
        Guard.check("role", role);

        final ConnectionProvider provider = databasePools.computeIfAbsent(getKey(database), _ -> create(database));

        final Connection connection = provider.getConnection();
        try {
            // Security check on the role.
            getSecurityProvider().onConnection(connection, role);

            // Security check passed, set role on the connection.
            setRole(connection, role);

            return connection;

        } catch (final SQLException e) {
            // Security check failed.
            connection.close();
            throw e;
        }
    }

    private ConnectionProvider create(final String database) {

        final ConnectionProvider dataSource = getDataSource(database);

        // Initialize JDBC Url to the Server host and port.
        dataSource.setJdbcUrl(this);

        for (final DataSourceListener listener : dataSourceListeners) {
            LOGGER.debug("Data source listener {}.onConfigure() for database '{}'",
                    listener.getClass().getSimpleName(), database);
            listener.onConfigure(dataSource);
        }

        final String key = getKey(database);
        final DatabaseListener listener = dataBaseListeners.get(key);
        if (listener != null) {
            LOGGER.debug("Database listener onConfigure() '{}'", database);
            listener.onConfigure(dataSource);
        }

        LOGGER.debug("Creating data source: {}", dataSource.getJdbcUrl());

        // Create the first connection to validate settings and
        // initialize the connection pool.
        try (final Connection connection = dataSource.getConnection()) {

            getSecurityProvider().onLogin(connection, dataSource.getUsername());
            return dataSource;

        } catch (final SQLException e) {
            dataSource.close();
            throw new CreateDataSourceException(e);
        }
    }

    @Override
    public final synchronized void close() {
        try {
            for (final ConnectionProvider provider : databasePools.values()) {
                LOGGER.info("Closing {}@{}", provider.getUsername(), provider.getJdbcUrl());
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
