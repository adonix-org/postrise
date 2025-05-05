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
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The base implementation of the {@link Server} interface.
 */
abstract class PostriseServer implements DataSourceListener, Server {

    private static final Logger LOGGER = LogManager.getLogger(PostriseServer.class);

    PostriseServer() {

        // This server will be notified first of all data source events.
        addListener(this);

        LOGGER.info("{}: initializing server", this);
        onInit();
    }

    /**
     * Subclasses will create and return a new instance of a
     * {@link ConnectionProvider} implementation.
     * 
     * @param databaseName - the database for the new {@link ConnectionProvider}.
     * @return a new {@link ConnectionProvider}.
     * @see PostriseDataSource
     */
    protected abstract ConnectionProvider createDataSource(final String databaseName);

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    @Override
    public final Set<String> getDatabaseNames() {
        return databasePools.keySet();
    }

    @Override
    public final Connection getConnection(final String databaseName) throws SQLException {
        return getDataSource(databaseName).getConnection();
    }

    @Override
    public final Connection getConnection(final String databaseName, final String roleName) throws SQLException {
        return getDataSource(databaseName).getConnection(roleName);
    }

    @Override
    public final DataSourceContext getDataSource(final String databaseName) {
        Guard.check("databaseName", databaseName);
        return getConnectionProvider(getKey(databaseName));
    }

    private ConnectionProvider getConnectionProvider(final String databaseName) {
        return databasePools.computeIfAbsent(databaseName, this::create);
    }

    private ConnectionProvider create(final String databaseName) {
        return isOpenThen(() -> doCreate(databaseName));
    }

    /**
     * Create the data source for the specific database on-demand. On exception,
     * the data source will be closed if it was opened, and the thrown exception
     * will be wrapped by a {@link CreateDataSourceException}
     * 
     * @param databaseName - the name of the database for creating the
     *                     {@link ConnectionProvider}.
     * @return a valid and configured {@link ConnectionProvider} implementation.
     * @throws SQLException
     */
    private ConnectionProvider doCreate(final String databaseName) {

        final ConnectionProvider provider = createDataSource(databaseName);

        // Listeners can configure the data source in this event.
        try {
            onBeforeCreate(provider);
        } catch (final Exception e) {
            throw new CreateDataSourceException(e);
        }

        // Create the first connection to validate settings, initialize the connection
        // pool, and send events to all listeners including "this".
        try (final Connection connection = provider.getConnection()) {

            provider.getRoleSecurity().onLogin(provider, connection);

            onAfterCreate(provider);

            return provider;

        } catch (final Exception e) {
            runCatch(provider::close);
            throw new CreateDataSourceException(e);
        }
    }

    // --------------------------------------------------------------------------
    // LISTENERS - Manage listeners to receive data source events.
    // --------------------------------------------------------------------------

    private final Set<DataSourceListener> dataSourceListeners = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Map<String, DatabaseListener> databaseListeners = new ConcurrentHashMap<>();

    @Override
    public final void addListener(final DataSourceListener listener) {
        Guard.check("listener", listener);
        if (!isOpenThen(() -> dataSourceListeners.add(listener)).booleanValue()) {
            LOGGER.error("{}: Data source listener \"{}\" already exists", this, listener);
        }
    }

    @Override
    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        if (isOpenThen(() -> databaseListeners.putIfAbsent(getKey(listener), listener)) != null) {
            LOGGER.error("{}: Database listener \"{}\" already exists", this, listener.getDatabaseName());
        }
    }

    // --------------------------------------------------------------------------
    // SERVER STATE - Thread-safe management of this server state.
    // --------------------------------------------------------------------------

    /**
     * A {@link Server} should be in one of three states.
     */
    private enum ServerState {
        OPEN, CLOSING, CLOSED
    }

    /**
     * Initialize the {@link Server} state to {@link ServerState#OPEN}
     */
    private ServerState state = ServerState.OPEN;

    private final ReadWriteLock stateLock = new ReentrantReadWriteLock();
    private final Lock readState = stateLock.readLock();
    private final Lock writeState = stateLock.writeLock();

    private <T> T isOpenThen(final Supplier<T> action) {
        readState.lock();
        try {
            switch (state) {
                case OPEN:
                    return action.get();
                case CLOSING:
                    throw new IllegalStateException(this + " is closing");
                case CLOSED:
                default:
                    throw new IllegalStateException(this + " is closed");
            }
        } finally {
            readState.unlock();
        }
    }

    // --------------------------------------------------------------------------
    // HELPERS - Utility functions.
    // --------------------------------------------------------------------------

    /**
     * Allows an {@link Exception} to be thrown from the {@link #run()} method.
     * 
     * @see PostriseServer#runCatch(ActionThrows) runCatch()
     */
    @FunctionalInterface
    protected interface ActionThrows {
        /**
         * Executes the action, potentially throwing a checked exception.
         *
         * @throws Exception if an error occurs during execution.
         */
        void run() throws Exception;
    }

    /**
     * Run the {@link ActionThrows} function. Any exception is caught and sent to
     * the {@link #onException(Exception)} event.
     * 
     * @param action - the action to be run.
     */
    protected final void runCatch(final ActionThrows action) {
        Guard.check("action", action);
        try {
            action.run();
        } catch (final Exception e) {
            try {
                LOGGER.error("{}: {}", this, e);
                onException(e);
            } catch (final Exception ex) {
                LOGGER.error("{}: {}", this, ex);
            }
        }
    }

    private static final String getKey(final String database) {
        return database.trim();
    }

    private static final String getKey(final DatabaseListener listener) {
        return getKey(listener.getDatabaseName());
    }

    // --------------------------------------------------------------------------
    // STATUS - Return the cumulative TOTAL, ACTIVE and IDLE connection counts.
    // --------------------------------------------------------------------------

    private int getStatus(final ToIntFunction<DataSourceContext> function) {
        int total = 0;
        for (final DataSourceContext context : databasePools.values()) {
            total += function.applyAsInt(context);
        }
        return total;
    }

    @Override
    public final int getTotalConnections() {
        return getStatus(DataSourceContext::getTotalConnections);
    }

    @Override
    public final int getActiveConnections() {
        return getStatus(DataSourceContext::getActiveConnections);
    }

    @Override
    public final int getIdleConnections() {
        return getStatus(DataSourceContext::getIdleConnections);
    }

    @Override
    public final int getThreadsAwaitingConnection() {
        return getStatus(DataSourceContext::getThreadsAwaitingConnection);
    }

    // --------------------------------------------------------------------------
    // SEND EVENTS - Send data source events to listeners including "this".
    // --------------------------------------------------------------------------

    /**
     * Send an event to a {@link DataSourceListener}.
     */
    @FunctionalInterface
    private interface DataSourceEvent {
        void sendTo(DataSourceListener listener);
    }

    private void doEvent(final DataSourceContext context, final DataSourceEvent event) {
        for (final DataSourceListener listener : dataSourceListeners) {
            runCatch(() -> event.sendTo(listener));
        }
        final DatabaseListener listener = databaseListeners.get(context.getDatabaseName());
        if (listener != null) {
            runCatch(() -> event.sendTo(listener));
        }
    }

    /**
     * Exceptions thrown by the beforeCreate events will prevent creation of the
     * data source. This is intentional.
     * 
     * @param settings
     */
    private void onBeforeCreate(final DataSourceSettings settings) {
        for (final DataSourceListener listener : dataSourceListeners) {
            listener.beforeCreate(settings);
        }
        final DatabaseListener listener = databaseListeners.get(settings.getDatabaseName());
        if (listener != null) {
            listener.beforeCreate(settings);
        }
        LOGGER.info("{}: creating data source: {}...", this, settings);
    }

    private void onAfterCreate(final DataSourceContext context) {
        doEvent(context, listener -> listener.afterCreate(context));
        LOGGER.info("{}: data source created: {}", this, context);
    }

    private void onBeforeClose(final DataSourceContext context) {
        LOGGER.info("{}: {} closing...", this, context);
        doEvent(context, listener -> listener.beforeClose(context));
    }

    private void onAfterClose(final DataSourceContext context) {
        doEvent(context, listener -> listener.afterClose(context));
        LOGGER.info("{}: {} closed", this, context);
    }

    // --------------------------------------------------------------------------
    // SERVER EVENTS - Server events which can be overridden.
    // --------------------------------------------------------------------------

    /**
     * Event will be dispatched during {@link Server} construction.
     */
    protected void onInit() {
    }

    /**
     * Event will be dispatched before the {@link Server} closes.
     */
    protected void beforeClose() {
    }

    /**
     * Event will be dispatched after the {@link Server} closes.
     */
    protected void afterClose() {
    }

    /**
     * Event will be dispatched by exceptions thrown during
     * {@link PostriseServer#runCatch(ActionThrows) runCatch()}.
     * 
     * @param e - the exception thrown.
     */
    protected void onException(final Exception e) {
    }

    // --------------------------------------------------------------------------
    // CLOSE - Clean shutdown of the server and data sources.
    // --------------------------------------------------------------------------

    @Override
    public final void close() {
        writeState.lock();
        try {
            if (state != ServerState.OPEN) {
                LOGGER.warn("{}: extra close request ignored", this);
                return;
            }
            state = ServerState.CLOSING;
            LOGGER.info("{}: server closing...", this);

            runCatch(this::beforeClose);
            for (final ConnectionProvider provider : databasePools.values()) {
                onBeforeClose(provider);
                runCatch(provider::close);
                onAfterClose(provider);
            }
            runCatch(databaseListeners::clear);
            runCatch(databasePools::clear);

            state = ServerState.CLOSED;
            LOGGER.info("{}: server closed", this);

            runCatch(this::afterClose);

        } finally {
            writeState.unlock();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
