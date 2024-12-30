package org.adonix.postrise;

public interface DatabaseListener extends DataSourceListener {

    String getDatabaseName();
}
