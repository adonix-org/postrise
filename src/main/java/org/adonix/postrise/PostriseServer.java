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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger();

    private ReentrantReadWriteLock closeLock = new ReentrantReadWriteLock();
    private ReadLock read = closeLock.readLock();
    private WriteLock write = closeLock.writeLock();
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

    private final Set<DataSourceListener> dataSourceListeners = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Map<String, DatabaseListener> databaseListeners = new ConcurrentHashMap<>();

    PostriseServer() {
        addListener(this);
    }

    public final void addListener(final DataSourceListener listener) {
        Guard.check("listener", listener);
        read.lock();
        try {
            Guard.check(this, isClosed);
            dataSourceListeners.add(listener);
        } finally {
            read.unlock();
        }
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        read.lock();
        try {
            Guard.check(this, isClosed);
            if (databaseListeners.put(getKey(listener), listener) != null) {
                LOGGER.warn("Overwriting existing configuration for database {}", listener.getDatabaseName());
            }
        } finally {
            read.unlock();
        }
    }

    protected final Set<String> getDatabaseNames() {
        read.lock();
        try {
            Guard.check(this, isClosed);
            return databasePools.keySet();
        } finally {
            read.unlock();
        }
    }

    public final DataSourceContext getDataSource(final String databaseName) {
        Guard.check("databaseName", databaseName);
        read.lock();
        try {
            Guard.check(this, isClosed);
            return getConnectionProvider(databaseName);
        } finally {
            read.unlock();
        }
    }

    private final ConnectionProvider getConnectionProvider(final String databaseName) {
        return databasePools.computeIfAbsent(getKey(databaseName),
                _ -> create(databaseName));
    }

    @Override
    public final Connection getConnection(final String databaseName, final String roleName) throws SQLException {

        Guard.check("databaseName", databaseName);
        Guard.check("roleName", roleName);

        read.lock();
        try {
            Guard.check(this, isClosed);

            final ConnectionProvider provider = getConnectionProvider(databaseName);
            final Connection connection = provider.getConnection();
            try {

                provider.onConnection(provider, connection, roleName);
                provider.setRole(connection, roleName);
                return connection;

            } catch (final SQLException e) {
                connection.close();
                onError(provider, e);
                throw e;
            }
        } finally {
            read.unlock();
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
        onBeforeCreate(provider);

        // Create the first connection to validate settings, initialize the connection
        // pool, and send events to all listeners including this class.
        try (final Connection connection = provider.getConnection()) {

            provider.onLogin(provider, connection);
            onAfterCreate(provider);
            return provider;

        } catch (final SQLException e) {
            provider.close();
            onError(provider, e);
            throw new CreateDataSourceException(e);
        }
    }

    private String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void afterCreate(final DataSourceContext context) {
        LOGGER.debug("{} data source created: {}", getClassName(), context.getJdbcUrl());
    }

    @Override
    public void beforeClose(final DataSourceContext context) {
        LOGGER.debug("Closing {}@{} for {}...", context.getLoginRole(), context.getJdbcUrl(), getClassName());
    }

    @Override
    public void afterClose(final DataSourceContext context) {
        LOGGER.debug("{}@{} for {} Closed", context.getLoginRole(), context.getJdbcUrl(), getClassName());
    }

    protected void beforeClose() {
        LOGGER.debug("{}.beforeClose()", getClassName());
    }

    protected void afterClose() {
        LOGGER.debug("{}.afterClose()", getClassName());
    }

    protected void onError(final ConnectionProvider provider, final Throwable t) {
        LOGGER.error("{} {} {}", getClassName(), provider.getJdbcUrl(), t.getMessage());
    }

    @Override
    public final synchronized void close() {
        if (isClosed) {
            return;
        }
        write.lock();
        try {
            beforeClose();
            for (final ConnectionProvider provider : databasePools.values()) {
                onBeforeClose(provider);
                try {
                    provider.close();
                } catch (final Exception e) {
                    onError(provider, e);
                } finally {
                    onAfterClose(provider);
                }
            }
        } finally {
            databaseListeners.clear();
            databasePools.clear();
            afterClose();
            isClosed = true;
            write.unlock();
        }
    }

    private final void onBeforeCreate(final DataSourceContext context) {
        for (final DataSourceListener listener : dataSourceListeners) {
            listener.beforeCreate(context);
        }

        final DatabaseListener listener = databaseListeners.get(context.getDatabaseName());
        if (listener != null) {
            listener.beforeCreate(context);
        }
    }

    private final void onAfterCreate(final DataSourceContext context) {
        for (final DataSourceListener listener : dataSourceListeners) {
            listener.afterCreate(context);
        }

        final DatabaseListener listener = databaseListeners.get(context.getDatabaseName());
        if (listener != null) {
            listener.afterCreate(context);
        }
    }

    private final void onBeforeClose(final DataSourceContext context) {
        for (final DataSourceListener listener : dataSourceListeners) {
            listener.beforeClose(context);
        }

        final DatabaseListener listener = databaseListeners.get(context.getDatabaseName());
        if (listener != null) {
            listener.beforeClose(context);
        }
    }

    private final void onAfterClose(final DataSourceContext context) {
        for (final DataSourceListener listener : dataSourceListeners) {
            listener.afterClose(context);
        }

        final DatabaseListener listener = databaseListeners.get(context.getDatabaseName());
        if (listener != null) {
            listener.afterClose(context);
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
