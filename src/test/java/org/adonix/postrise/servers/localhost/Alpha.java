package org.adonix.postrise.servers.localhost;

import java.sql.SQLException;
import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.PostgresServer;
import org.junit.Test;

public class Alpha extends PostgresServer {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setUsername("postrise_user");
    }

    @Test
    public void test() throws SQLException {
        try (Alpha a = new Alpha()) {
            a.getConnection("postrise", "postrise_test");
        }
    }
}
