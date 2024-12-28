package org.adonix.postrise.servers;

import org.adonix.postrise.Server;
import org.adonix.postrise.servers.localhost.Localhost;

public class Servers {

    private static final Server LOCALHOST = new Localhost();

    public static final Server getLocalhost() {
        return LOCALHOST;
    }
}
