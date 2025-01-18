package org.adonix.postrise;

public interface ConnectionPoolStatus extends ConnectionPoolSettings {

    int getActiveConnections();

    int getIdleConnections();

    int getTotalConnections();
}
