package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    @Override
    public void onCreate(final ConnectionSettings settings) {
        settings.setLoginRole("beta_login");
        settings.setLoginPassword("helloworld");
    }
}
