package org.adonix.postrise;

public class DeltaServer extends PostgresTestServer {

    private static final Server instance = new DeltaServer();

    private DeltaServer() {
    }

    /**
     * @return the singleton {@link GammaServer}.
     */
    public static final Server getInstance() {
        return instance;
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setUsername("");
        settings.setPassword("");
    }
}
