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
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger();

    enum ServerState {
        OPEN, CLOSING, CLOSED
    }

    private ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();
    private ReadLock readState = stateLock.readLock();
    private WriteLock writeState = stateLock.writeLock();

    private volatile ServerState state = ServerState.OPEN;

    ServerState getState() {
        return state;
    }

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
        isOpen(() -> dataSourceListeners.add(listener));
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        if (isOpen(() -> databaseListeners.put(getKey(listener), listener)) != null) {
            LOGGER.warn("{}: Overwriting existing configuration for database {}", this, listener.getDatabaseName());
        }
    }

    protected final Set<String> getDatabaseNames() {
        return databasePools.keySet();
    }

    protected final DataSourceContext getDataSource(final String databaseName) {
        Guard.check("databaseName", databaseName);
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

    private final ConnectionProvider create(final String databaseName) {
        return isOpen(() -> doCreate(databaseName));
    }

    /**
     * 
     * @param databaseName - the name of the database for creating the
     *                     {@link ConnectionProvider}.
     * @return a valid and configured {@link ConnectionProvider} implementation.
     */
    private final ConnectionProvider doCreate(final String databaseName) {

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
    }

    /**
     * HELPERS
     */
    private void inOrder(final Runnable action) {
        try {
            action.run();
        } catch (final Exception e) {
            onException(e);
        }
    }

    private <T> T isOpen(final Supplier<T> action) {
        readState.lock();
        try {
            Guard.check(this);
            return action.get();
        } finally {
            readState.unlock();
        }
    }

    private static final String getKey(final String database) {
        return database.trim();
    }

    private static final String getKey(final DatabaseListener listener) {
        return getKey(listener.getDatabaseName());
    }

    /**
     * EVENTS
     */

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

    @Override
    public void afterCreate(final DataSourceContext context) {
        LOGGER.debug("{} data source created: {}", this, context.getJdbcUrl());
    }

    @Override
    public void beforeClose(final DataSourceContext context) {
        LOGGER.debug("{}: closing {}@{}...", this, context.getLoginRole(), context.getJdbcUrl());
    }

    @Override
    public void afterClose(final DataSourceContext context) {
        LOGGER.debug("{}: {}@{} closed", this, context.getLoginRole(), context.getJdbcUrl());
    }

    protected void beforeClose() {
        LOGGER.debug("{}.beforeClose()", this);
    }

    protected void afterClose() {
        LOGGER.debug("{}.afterClose()", this);
    }

    protected void onException(final ConnectionProvider provider, final Exception e) {
        LOGGER.error("{} {} {}", this, provider.getJdbcUrl(), e);
    }

    protected void onException(final Exception e) {
        LOGGER.error("{} {}", this, e);
    }

    /**
     * CLOSE
     */
    @Override
    public final void close() {
        writeState.lock();
        try {
            if (state == ServerState.CLOSED) {
                return;
            }
            state = ServerState.CLOSING;

            inOrder(this::beforeClose);
            for (final ConnectionProvider provider : databasePools.values()) {
                inOrder(() -> onBeforeClose(provider));
                inOrder(provider::close);
                inOrder(() -> onAfterClose(provider));
            }
            inOrder(databaseListeners::clear);
            inOrder(databasePools::clear);

            state = ServerState.CLOSED;
            inOrder(this::afterClose);

        } finally {
            writeState.unlock();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
