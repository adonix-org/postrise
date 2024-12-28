package org.adonix.postrise;

public interface DatabaseConnectionListener extends DataSourceListener {

    String getDatabaseName();
}
