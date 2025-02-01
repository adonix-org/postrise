package org.adonix.postrise;

import java.util.Optional;

interface ConnectionPoolStatus {

    Optional<Integer> getActiveConnections();

    Optional<Integer> getIdleConnections();

    Optional<Integer> getTotalConnections();
}
