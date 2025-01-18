package org.adonix.postrise;

interface ConnectionPoolStatus {

    int getActiveConnections();

    int getIdleConnections();

    int getTotalConnections();
}
