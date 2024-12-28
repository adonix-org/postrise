package org.adonix.postrise;

public interface DatabaseConnectionListener extends ConnectionListener {

    String getDatabaseName();
}
