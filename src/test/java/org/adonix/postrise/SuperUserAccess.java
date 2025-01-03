package org.adonix.postrise;

public class SuperUserAccess extends PostgresTestServer implements DataSourceListener {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(container.withDatabaseName(settings.getDatabase()).getJdbcUrl());
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }
}
