package org.adonix.postrise;

public class RestrictedUserAccess extends PostgresTestServer implements DataSourceListener {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(container.withDatabaseName(settings.getDatabase()).getJdbcUrl());
        settings.setUsername("login_user");
        settings.setPassword("helloworld");
    }
}
