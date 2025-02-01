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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger();

    private volatile boolean isClosed = false;

    /**
     * Subclasses will create and return a new instance of a
     * {@link ConnectionProvider} implementation.
     * 
     * @param database - the database for the new {@link ConnectionProvider}.
     * @return a new {@link ConnectionProvider}.
     * @see PostriseDataSource
     */
    protected abstract ConnectionProvider createConnectionProvider(final String database);

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    private final Set<DataSourceListener> dataSourceListeners = Collections
            .synchronizedSet(new LinkedHashSet<>());

    private final Map<String, DatabaseListener> databaseListeners = new ConcurrentHashMap<>();

    PostriseServer() {
        addListener(this);
    }

    public final void addListener(final DataSourceListener listener) {
        Guard.check("listener", listener);
        dataSourceListeners.add(listener);
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        if (databaseListeners.put(getKey(listener), listener) != null) {
            LOGGER.warn("Overwriting existing configuration for database '{}'", listener.getDatabaseName());
        }
    }

    protected final Set<String> getDatabases() {
        return databasePools.keySet();
    }

    public final DataSourceContext getDataSource(final String databaseName) {
        Guard.check("databaseName", databaseName);
        Guard.check(this, isClosed);
        return getConnectionProvider(databaseName);
    }

    private final ConnectionProvider getConnectionProvider(final String databaseName) {
        final ConnectionProvider provider = databasePools.computeIfAbsent(getKey(databaseName),
                _ -> create(databaseName));
        return provider;
    }

    @Override
    public final Connection getConnection(final String databaseName, final String roleName) throws SQLException {

        Guard.check("databaseName", databaseName);
        Guard.check("roleName", roleName);
        Guard.check(this, isClosed);

        final ConnectionProvider provider = getConnectionProvider(databaseName);

        final Connection connection = provider.getConnection();
        try {

            provider.onConnection(provider, connection, roleName);
            provider.setRole(connection, roleName);
            return connection;

        } catch (final SQLException e) {
            connection.close();
            throw e;
        }
    }

    /**
     * 
     * @param databaseName - the name of the database for creating the
     *                     {@link ConnectionProvider}.
     * @return a valid and configured {@link ConnectionProvider} implementation.
     */
    private final ConnectionProvider create(final String databaseName) {

        final ConnectionProvider provider = createConnectionProvider(databaseName);

        // Set the JDBC Url for this provider.
        provider.setJdbcUrl(getHostName(), getPort());

        for (final DataSourceListener listener : dataSourceListeners) {
            listener.beforeCreate(provider);
        }

        final DatabaseListener listener = databaseListeners.get(getKey(databaseName));
        if (listener != null) {
            listener.beforeCreate(provider);
        }

        // Create the first connection to validate settings, initialize the connection
        // pool, and send to the onLogin to this class and subclasses.
        try (final Connection connection = provider.getConnection()) {

            provider.onLogin(provider, connection);
            afterCreate(provider);
            return provider;

        } catch (final SQLException e) {
            provider.close();
            throw new CreateDataSourceException(provider.getJdbcUrl(), e);
        }
    }

    private String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void afterCreate(final DataSourceContext context) {
        LOGGER.debug("{} data source created: {}", getClassName(), context.getJdbcUrl());
    }

    protected void beforeClose() {
        LOGGER.debug("{}.beforeClose()", getClassName());
    }

    protected void afterClose() {
        LOGGER.debug("{}.afterClose()", getClassName());
    }

    @Override
    public void beforeClose(final DataSourceContext context) {
        LOGGER.debug("Closing {}@{} for {}...", context.getLoginRole(), context.getJdbcUrl(), getClassName());
    }

    @Override
    public void afterClose(final DataSourceContext context) {
        LOGGER.debug("{}@{} for {} Closed", context.getLoginRole(), context.getJdbcUrl(), getClassName());
    }

    @Override
    public final synchronized void close() {
        if (isClosed) {
            return;
        }
        try {
            beforeClose();
            for (final ConnectionProvider provider : databasePools.values()) {
                beforeClose(provider);
                try {
                    provider.close();
                } catch (final Exception e) {
                    LOGGER.error("Closing {}@{}", provider.getLoginRole(), provider.getJdbcUrl(), e);
                } finally {
                    afterClose(provider);
                }
            }
        } finally {
            databasePools.clear();
            this.isClosed = true;
            afterClose();
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
