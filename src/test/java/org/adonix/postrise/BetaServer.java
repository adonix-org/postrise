package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    @Override
    public void onCreate(final ConnectionConfiguration config) {
        config.setLoginRole("beta_login");
        config.setLoginPassword("helloworld");
    }
}
