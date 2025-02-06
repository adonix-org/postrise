package org.adonix.postrise.servers;

import org.adonix.postrise.DatabaseListener;

class PostriseDatabase implements DatabaseListener {

    @Override
    public String getDatabaseName() {
        return "postrise";
    }
}
