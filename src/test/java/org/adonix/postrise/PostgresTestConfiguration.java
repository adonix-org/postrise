package org.adonix.postrise;

import org.testcontainers.containers.JdbcDatabaseContainer;

public class PostgresTestConfiguration implements ConfigurationListener {

    private static final JdbcDatabaseContainer<PostgresDockerContainer> container = new PostgresDockerContainer();

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(container.withDatabaseName(settings.getDatabaseName()).getJdbcUrl());
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }

    public static JdbcDatabaseContainer<PostgresDockerContainer> getContainer() {
        return container;
    }
}
