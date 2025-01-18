package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    @Override
    public void onCreate(final ConnectionCreator creator) {
        creator.setLoginRole("beta_login");
        creator.setLoginPassword("helloworld");
    }
}
