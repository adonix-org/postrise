package org.adonix.postrise;

class BetaServer extends PostgresTestServer {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        settings.setLoginRole("beta_login");
        settings.setLoginPassword("helloworld");
    }
}
