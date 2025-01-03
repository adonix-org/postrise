package org.adonix.postrise;

public class RestrictedUserAccess extends PostgresTestServer implements DataSourceListener {

    @Override
    public void onConfigure(String database, ConnectionSettings settings) {
        settings.setJdbcUrl(container.withDatabaseName(database).getJdbcUrl());
        settings.setUsername("login_user");
        settings.setPassword("helloworld");
    }
}
