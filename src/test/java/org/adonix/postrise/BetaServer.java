package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setUsername("beta_login");
        settings.setPassword("helloworld");
    }
}
