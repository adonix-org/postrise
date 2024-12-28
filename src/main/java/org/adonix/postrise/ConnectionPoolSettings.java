package org.adonix.postrise;

interface ConnectionPoolSettings {

    // TODO: Compare these methods to other connection pool implementations for
    // common methods to make this more generic.

    void setMaxPoolSize(int size);

    int getMaxPoolSize();

    void setConnectionTimeout(long connectionTimeoutMs);

    long getConnectionTimeout();

    void setValidationTimeout(long validationTimeoutMs);

    long getValidationTimeout();

    void setIdleTimeout(long idleTimeoutMs);

    long getIdleTimeout();

    void setMinIdle(int minIdle);

    int getMinIdle();

    void setLeakDetectionThreshold(long leakDetectionThresholdMs);

    long getLeakDetectionThreshold();

    void setMaxLifetime(long maxLifetimeMs);

    long getMaxLifetime();

    int getActiveConnections();

    int getIdleConnections();

    int getTotalConnections();

    int getAvailableProcessors();

    void close();
}
