package org.adonix.postrise;

public class GammaServer extends PostgresTestServer {

    private static final Server instance = new GammaServer();

    private GammaServer() {
    }

    /**
     * @return the singleton {@link GammaServer}.
     */
    static final Server getInstance() {
        return instance;
    }
}
