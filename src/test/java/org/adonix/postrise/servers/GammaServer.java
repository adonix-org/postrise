package org.adonix.postrise.servers;

public class GammaServer extends PostgresDocker {

    public GammaServer() {
        addListener(new PostriseDatabase());
    }
}
