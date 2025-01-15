package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    private static final Server instance = new BetaServer();

    private BetaServer() {
    }

    /**
     * @return the singleton{@link BetaServer}.
     */
    public static final Server getInstance() {
        return instance;
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        super.onConfigure(settings);
        settings.setUsername("beta_login");
        settings.setPassword("helloworld");
    }
}
