package org.adonix.postrise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.adonix.postrise.security.DefaultUserSecurity;
import org.adonix.postrise.security.UserSecurityProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements Server, DataSourceListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String SQL_SET_ROLE = "SELECT set_config('ROLE', ?, false)";

    private static final int DEFAULT_POSTGRES_PORT = 5432;

    private static final String DEFAULT_HOST_NAME = "localhost";

    private static final UserSecurityProvider DEFAULT_USER_SECURITY = new DefaultUserSecurity();

    private final Map<String, DatabaseListener> dataBaseListeners = new HashMap<>();

    private final Set<DataSourceListener> dataSourceListeners = new HashSet<>();

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    public final void addListener(final DatabaseListener listener) {
        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());
        if (dataBaseListeners.put(getKey(listener), listener) != null) {
            LOGGER.warn("Overwriting existing settings for database '{}'", listener.getDatabaseName());
        }
    }

    public final void addListener(final DataSourceListener listener) {
        Guard.check("listener", listener);
        dataSourceListeners.add(listener);
    }

    protected UserSecurityProvider getUserSecurity() {
        return DEFAULT_USER_SECURITY;
    }

    protected ConnectionProvider getConnectionProvider(final String database) {
        return new PostgresDataSource(this, database);
    }

    @Override
    public int getPort() {
        return DEFAULT_POSTGRES_PORT;
    }

    @Override
    public String getHostname() {
        return DEFAULT_HOST_NAME;
    }

    @Override
    public final Connection getConnection(final String database, final String role) throws SQLException {

        Guard.check("database", database);
        Guard.check("role", role);

        final ConnectionProvider provider = databasePools.computeIfAbsent(getKey(database), _ -> create(database));

        final Connection connection = provider.getConnection();
        try (final PreparedStatement stmt = connection.prepareStatement(SQL_SET_ROLE)) {

            // Security check on the role.
            getUserSecurity().onConnection(connection, role);

            // Set the role on the connection to be returned.
            stmt.setString(1, role);
            stmt.execute();

            return connection;

        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

    private ConnectionProvider create(final String database) {

        final ConnectionProvider provider = getConnectionProvider(database);

        onConfigure(provider);

        for (final DataSourceListener listener : dataSourceListeners) {
            listener.onConfigure(provider);
        }

        LOGGER.debug("Creating datasource: {}", provider.getJdbcUrl());

        final String key = getKey(database);
        final DatabaseListener listener = dataBaseListeners.get(key);
        if (listener != null) {
            LOGGER.debug("Data source listener onCreate() '{}'", database);
            listener.onConfigure(provider);
        }

        // Create the first connection to validate settings and
        // initialize the connection pool.
        try (final Connection connection = provider.getConnection()) {

            getUserSecurity().onLogin(connection, provider.getUsername());
            return provider;

        } catch (SQLException e) {
            provider.close();
            throw new CreateDataSourceException(e);
        }
    }

    @Override
    public final synchronized void close() {
        try {
            for (final ConnectionProvider provider : databasePools.values()) {
                LOGGER.debug("Closing {}@{}", provider.getUsername(), provider.getJdbcUrl());
                try {
                    provider.close();
                } catch (Exception e) {
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
        return this.getHostname() + ":" + this.getPort();
    }
}
