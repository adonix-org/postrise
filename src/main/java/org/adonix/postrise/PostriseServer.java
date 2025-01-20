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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.adonix.postrise.security.SecurityProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements DataSourceEvent, Server {

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

    protected abstract SecurityProvider getSecurityProvider();

    protected abstract void setRole(final Connection connection, final String roleName) throws SQLException;

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    private final Set<DataSourceEvent> dataSourceListeners = Collections
            .synchronizedSet(new LinkedHashSet<>());

    private final Map<String, DatabaseEvent> databaseListeners = new ConcurrentHashMap<>();

    protected PostriseServer() {
        addListener(this);
    }

    public final void addListener(final DataSourceEvent listener) {
        Guard.check("listener", listener);
        dataSourceListeners.add(listener);
    }

    public final void addListener(final DatabaseEvent listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        if (databaseListeners.put(getKey(listener), listener) != null) {
            LOGGER.warn("Overwriting existing configuration for database '{}'", listener.getDatabaseName());
        }
    }

    protected final Set<String> getDatabases() {
        return databasePools.keySet();
    }

    protected final Optional<DataSourceContext> getDataSource(final String database) {

        Guard.check("database", database);
        return Optional.ofNullable(databasePools.get(getKey(database)));
    }

    @Override
    public void onCreate(final DataSourceSettings settings) {
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

        // Set the JDBC Url for this provider.
        connectionProvider.setJdbcUrl(getHostName(), getPort());

        for (final DataSourceEvent listener : dataSourceListeners) {
            listener.onCreate(connectionProvider);
        }

        final DatabaseEvent listener = databaseListeners.get(getKey(database));
        if (listener != null) {
            listener.onCreate(connectionProvider);
        }

        // Create the first connection to validate settings, initialize the connection
        // pool, and validate security for login user.
        try (final Connection connection = connectionProvider.getConnection()) {

            getSecurityProvider().onLogin(connection, connectionProvider.getLoginRole());
            afterCreate(connectionProvider);
            return connectionProvider;

        } catch (final SQLException e) {
            connectionProvider.close();
            throw new CreateDataSourceException(connectionProvider.getJdbcUrl(), e);
        }
    }

    private String getClassName() {
        return this.getClass().getSimpleName();
    }

    public void afterCreate(final DataSourceContext context) {
        LOGGER.debug("{} data source created: {}", getClassName(), context.getJdbcUrl());
    }

    protected void beforeClose() {
        LOGGER.debug("Closing Server {}", getClassName());
    }

    protected void afterClose() {
        LOGGER.debug("Server {} Closed", getClassName());
    }

    public void beforeClose(final DataSourceContext context) {
        LOGGER.debug("Closing {}@{} for {}...", context.getLoginRole(), context.getJdbcUrl(), getClassName());
    }

    public void afterClose(final DataSourceContext context) {
        LOGGER.debug("{}@{} for {} Closed", context.getLoginRole(), context.getJdbcUrl(), getClassName());
    }

    @Override
    public final synchronized void close() {
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
            afterClose();
        }
    }

    private static final String getKey(final String database) {
        return database.trim();
    }

    private static final String getKey(final DatabaseEvent listener) {
        return getKey(listener.getDatabaseName());
    }

    @Override
    public String toString() {
        return getHostName() + ":" + getPort();
    }
}
