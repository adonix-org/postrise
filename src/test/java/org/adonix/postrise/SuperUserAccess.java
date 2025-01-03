package org.adonix.postrise;

public class SuperUserAccess extends PostgresTestServer implements DataSourceListener {

    @Override
    public void onConfigure(String database, ConnectionSettings settings) {
        settings.setJdbcUrl(container.withDatabaseName(database).getJdbcUrl());
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }
}
