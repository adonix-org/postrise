package org.adonix.postrise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.adonix.postrise.exception.CreateDataSourceException;
import org.adonix.postrise.security.DefaultUserSecurity;
import org.adonix.postrise.security.SecurityEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PostriseServer implements Server, DataSourceListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String SQL_SET_ROLE = "SELECT set_config('ROLE', ?, false)";

    private static final int DEFAULT_POSTGRES_PORT = 5432;

    private static final String DEFAULT_HOST_NAME = "localhost";

    private static final SecurityEventListener DEFAULT_USER_SECURITY = new DefaultUserSecurity();

    private final Map<String, DatabaseConnectionListener> listeners = new HashMap<>();

    private final ConcurrentMap<String, ConnectionProvider> databasePools = new ConcurrentHashMap<>();

    protected final void add(final DatabaseConnectionListener listener) {

        Guard.check("listener", listener);
        Guard.check("listener.getDatabaseName()", listener.getDatabaseName());

        if (listeners.put(getKey(listener), listener) != null) {
            LOGGER.warn("Overwriting existing settings for database '{}'", listener.getDatabaseName());
        }
    }

    protected SecurityEventListener getUserSecurity() {
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

            // Perform security checks on the role.
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

        onCreate(provider);

        LOGGER.debug("Creating datasource: {}", provider.getJdbcUrl());

        final DatabaseConnectionListener listener = listeners.get(getKey(database));
        if (listener != null) {
            LOGGER.debug("Data source listener onCreate() '{}'", database);
            listener.onCreate(provider);
        }

        // Create the first connection to validate all settings.
        try (final Connection connection = provider.getConnection()) {

            // Check the login user.
            getUserSecurity().onLogin(connection, provider.getUsername());
            return provider;

        } catch (SQLException e) {
            provider.close();
            throw new CreateDataSourceException(e);
        }
    }

    @Override
    public final synchronized void close() {
        final Iterator<ConnectionProvider> iT = databasePools.values().iterator();
        try {
            while (iT.hasNext()) {
                final ConnectionProvider provider = iT.next();
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

    private static final String getKey(final DatabaseConnectionListener listener) {
        return getKey(listener.getDatabaseName());
    }
}
