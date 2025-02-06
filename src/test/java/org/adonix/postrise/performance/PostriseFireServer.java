package org.adonix.postrise.performance;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.servers.PostgresDocker;

class PostriseFireServer extends PostgresDocker {

    private static final PostriseFireServer instance = new PostriseFireServer();

    private PostriseFireServer() {
    }

    static PostriseFireServer getInstance() {
        return instance;
    }

    static Connection getConnection(final String role) throws SQLException {
        return instance.getConnection("postrise", role);
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setLoginRole("postrise");
    }
}
