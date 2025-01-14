package org.adonix.postrise;

class BetaServer extends PostgresContainerServer {

    private static final Server instance = new BetaServer();

    public static final Server getInstance() {
        return instance;
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        super.onConfigure(settings);
        settings.setUsername("login_user");
        settings.setPassword("helloworld");
    }
}
