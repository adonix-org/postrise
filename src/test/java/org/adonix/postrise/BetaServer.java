package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    @Override
    public void onConfigure(final ConnectionContext context) {
        context.setLoginRole("beta_login");
        context.setLoginPassword("helloworld");
    }
}
