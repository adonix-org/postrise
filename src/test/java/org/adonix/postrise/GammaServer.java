package org.adonix.postrise;

class GammaServer extends PostgresTestServer {

    public GammaServer() {
        addListener(new PostriseDatabase());
    }
}
