package org.adonix.postrise;

public interface ConnectionPoolStatus {

    int getActiveConnections();

    int getIdleConnections();

    int getTotalConnections();
}
