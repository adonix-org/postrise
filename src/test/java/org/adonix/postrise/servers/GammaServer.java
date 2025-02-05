package org.adonix.postrise.servers;

public class GammaServer extends PostgresTestServer {

    public GammaServer() {
        addListener(new PostriseDatabase());
    }
}
