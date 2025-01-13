package org.adonix.postrise.servers.localhost;

import static org.adonix.postrise.security.SecurityProviders.DISABLE_SECURITY;

import java.sql.SQLException;
import org.adonix.postrise.security.SecurityEventListener;
import org.junit.jupiter.api.Test;

public class Beta extends Alpha {

    @Override
    protected SecurityEventListener getSecurityProvider() {
        return DISABLE_SECURITY;
    }

    @Test
    public void run() throws SQLException {
        getConnection("test", "test").close();
    }
}
