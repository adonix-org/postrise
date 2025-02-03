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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger();

    private ReentrantReadWriteLock closeLock = new ReentrantReadWriteLock();
    private ReadLock readClosed = closeLock.readLock();
    private WriteLock writeClosed = closeLock.writeLock();
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
        readClosed.lock();
        try {
            Guard.check(this, isClosed);
            dataSourceListeners.add(listener);
        } finally {
            readClosed.unlock();
        }
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        readClosed.lock();
        try {
            Guard.check(this, isClosed);
            if (databaseListeners.put(getKey(listener), listener) != null) {
                LOGGER.warn("Overwriting existing configuration for database {}", listener.getDatabaseName());
            }
        } finally {
            readClosed.unlock();
        }
    }

    protected final Set<String> getDatabaseNames() {
        return databasePools.keySet();
    }

    public final DataSourceContext getDataSource(final String databaseName) {
        return getConnectionProvider(databaseName);
    }

    private final ConnectionProvider getConnectionProvider(final String databaseName) {
        return databasePools.computeIfAbsent(getKey(databaseName), _ -> create(databaseName));
    }

    @Override
    public final Connection getConnection(final String databaseName, final String roleName) throws SQLException {

        Guard.check("databaseName", databaseName);
        Guard.check("roleName", roleName);

        final ConnectionProvider provider = getConnectionProvider(databaseName);
        final Connection connection = provider.getConnection();
        try {

            provider.onConnection(provider, connection, roleName);
            provider.setRole(connection, roleName);
            return connection;

        } catch (final SQLException e) {
            connection.close();
            onException(provider, e);
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
        readClosed.lock();
        try {
            Guard.check(this, isClosed);

            final ConnectionProvider provider = createConnectionProvider(databaseName);
            provider.setJdbcUrl(getHostName(), getPort());
            onBeforeCreate(provider);

            // Create the first connection to validate settings, initialize the connection
            // pool, and send events to all listeners including this class.
            try (final Connection connection = provider.getConnection()) {

                provider.onLogin(provider, connection);
                onAfterCreate(provider);
                return provider;

            } catch (final Exception e) {
                provider.close();
                onException(provider, e);
                throw new CreateDataSourceException(e);
            }
        } finally {
            readClosed.unlock();
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

    protected void onException(final ConnectionProvider provider, final Throwable t) {
        LOGGER.error("{} {} {}", getClassName(), provider.getJdbcUrl(), t.getMessage());
    }

    protected void onExceptions(final List<Exception> exceptions) {
        for (final Exception e : exceptions) {
            LOGGER.error("{} {}", getClassName(), e.getMessage());
        }
    }

    @Override
    public final void close() {
        writeClosed.lock();
        try {
            if (isClosed) {
                return;
            }

            final List<Exception> exceptions = new LinkedList<>();
            safeExecute(this::beforeClose, exceptions);
            for (final ConnectionProvider provider : databasePools.values()) {
                safeExecute(() -> onBeforeClose(provider), exceptions);
                safeExecute(provider::close, exceptions);
                safeExecute(() -> onAfterClose(provider), exceptions);
            }
            safeExecute(databaseListeners::clear, exceptions);
            safeExecute(databasePools::clear, exceptions);
            safeExecute(this::afterClose, exceptions);

            if (exceptions != null && exceptions.size() > 0) {
                onExceptions(exceptions);
            }
            isClosed = true;
        } finally {
            writeClosed.unlock();
        }
    }

    private void safeExecute(final Runnable action, final List<Exception> exceptions) {
        try {
            action.run();
        } catch (final Exception e) {
            exceptions.add(e);
        }
    }

    private void doEvent(final DataSourceContext context, final Consumer<DataSourceListener> event) {
        for (DataSourceListener listener : dataSourceListeners) {
            event.accept(listener);
        }

        DatabaseListener listener = databaseListeners.get(context.getDatabaseName());
        if (listener != null) {
            event.accept(listener);
        }
    }

    private final void onBeforeCreate(final DataSourceContext context) {
        doEvent(context, listener -> listener.beforeCreate(context));
    }

    private final void onAfterCreate(final DataSourceContext context) {
        doEvent(context, listener -> listener.afterCreate(context));
    }

    private final void onBeforeClose(final DataSourceContext context) {
        doEvent(context, listener -> listener.beforeClose(context));
    }

    private final void onAfterClose(final DataSourceContext context) {
        doEvent(context, listener -> listener.afterClose(context));
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
