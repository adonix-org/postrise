package org.adonix.postrise;

public class BetaServer extends PostgresContainerServer {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        super.onConfigure(settings);
        settings.setUsername("login_user");
        settings.setPassword("helloworld");
    }
}
