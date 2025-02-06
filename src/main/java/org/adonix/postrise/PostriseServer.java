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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger(PostriseServer.class);

    PostriseServer() {
        addListener(this);
    }

    private enum ServerState {
        OPEN, CLOSING, CLOSED
    }

    private final ReadWriteLock stateLock = new ReentrantReadWriteLock();
    private final Lock readState = stateLock.readLock();
    private final Lock writeState = stateLock.writeLock();
    private ServerState state = ServerState.OPEN;

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

    public final void addListener(final DataSourceListener listener) {
        Guard.check("listener", listener);
        isOpenThen(() -> dataSourceListeners.add(listener));
    }

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        if (isOpenThen(() -> databaseListeners.put(getKey(listener), listener)) != null) {
            LOGGER.warn("{}: Overwriting existing configuration for database {}", this, listener.getDatabaseName());
        }
    }

    @Override
    public final Set<String> getDatabaseNames() {
        return databasePools.keySet();
    }

    @Override
    public final DataSourceContext getDataSource(final String databaseName) {
        return getConnectionProvider(databaseName);
    }

    private final ConnectionProvider getConnectionProvider(final String databaseName) {
        return databasePools.computeIfAbsent(getKey(databaseName), _ -> create(databaseName));
    }

    @Override
    public final Connection getConnection(final String databaseName) throws SQLException {

        Guard.check("databaseName", databaseName);
        return getConnectionProvider(databaseName).getConnection();
    }

    private final ConnectionProvider create(final String databaseName) {
        return isOpenThen(() -> doCreate(databaseName));
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
        // pool, and send events to all listeners including "this".
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

    /**
     * Runs the given action and any exception is caught to guarantee execution
     * order.
     * 
     * @param action
     */
    protected void runSafe(final RunnableThrows action) {
        try {
            action.run();
        } catch (final Exception e) {
            try {
                onException(e);
            } catch (final Exception ex) {
                LOGGER.error("{}: {}", this, ex);
            }
        }
    }

    private <T> T isOpenThen(final Supplier<T> action) {
        readState.lock();
        try {
            switch (state) {
                case OPEN:
                    return action.get();
                case CLOSING:
                    throw new IllegalStateException(this + " is closing");
                case CLOSED:
                    throw new IllegalStateException(this + " is closed");
                default:
                    throw new IllegalStateException(this + " state unknown");
            }
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

    private final void onBeforeCreate(final ConnectionProvider provider) {
        doEvent(provider, listener -> listener.beforeCreate(provider));
    }

    private final void onAfterCreate(final ConnectionProvider provider) {
        doEvent(provider, listener -> listener.afterCreate(provider));
    }

    private final void onBeforeClose(final ConnectionProvider provider) {
        doEvent(provider, listener -> listener.beforeClose(provider));
    }

    private final void onAfterClose(final ConnectionProvider provider) {
        doEvent(provider, listener -> listener.afterClose(provider));
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        LOGGER.info("{}: creating data source: {}...", this, settings.getJdbcUrl());
    }

    @Override
    public void afterCreate(final DataSourceContext context) {
        LOGGER.info("{}: data source created: {}", this, context.getJdbcUrl());
    }

    @Override
    public void beforeClose(final DataSourceContext context) {
        LOGGER.info("{}: closing {}...", this, context);
    }

    @Override
    public void afterClose(final DataSourceContext context) {
        LOGGER.info("{}: {} closed", this, context);
    }

    protected void beforeClose() {
        LOGGER.info("{}.beforeClose()", this);
    }

    protected void afterClose() {
        LOGGER.info("{}.afterClose()", this);
    }

    protected void onException(final DataSourceContext context, final Exception e) {
        LOGGER.error("{}: {} {}", this, context, e);
    }

    protected void onException(final Exception e) {
        LOGGER.error("{}: {}", this, e);
    }

    /**
     * CLOSE
     */
    @Override
    public final void close() {
        writeState.lock();
        try {
            if (state != ServerState.OPEN) {
                LOGGER.warn("{}: extra close request ignored", this);
                return;
            }
            state = ServerState.CLOSING;

            runSafe(this::beforeClose);
            for (final ConnectionProvider provider : databasePools.values()) {
                runSafe(() -> onBeforeClose(provider));
                runSafe(provider::close);
                runSafe(() -> onAfterClose(provider));
            }
            runSafe(databaseListeners::clear);
            runSafe(databasePools::clear);

            state = ServerState.CLOSED;
            runSafe(this::afterClose);

        } finally {
            writeState.unlock();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
