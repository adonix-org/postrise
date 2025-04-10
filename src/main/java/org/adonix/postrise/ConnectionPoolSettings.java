/*
 * Copyright (C) 2025 Ty Busby
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adonix.postrise;

/**
 * Settings for the connection pool. Documentation is from HikariCP.
 */
interface ConnectionPoolSettings {

    /**
     * This property controls the maximum size that the pool is allowed to reach,
     * including both idle and in-use connections. Basically this value will
     * determine the maximum number of actual connections to the database backend. A
     * reasonable value for this is best determined by your execution environment.
     * When the pool reaches this size, and no idle connections are available, calls
     * to getConnection() will block for up to connectionTimeout milliseconds before
     * timing out.
     * 
     * @param size - the maximum number of connections in the pool.
     */
    void setMaxPoolSize(int size);

    /**
     * This property controls the maximum size that the pool is allowed to reach,
     * including both idle and in-use connections. Basically this value will
     * determine the maximum number of actual connections to the database backend. A
     * reasonable value for this is best determined by your execution environment.
     * When the pool reaches this size, and no idle connections are available, calls
     * to getConnection() will block for up to connectionTimeout milliseconds before
     * timing out.
     * 
     * @return the maximum number of connections in the pool.
     */
    int getMaxPoolSize();

    /**
     * This property controls the maximum number of milliseconds that a client
     * will wait for a connection from the pool. If this time is exceeded without a
     * connection becoming available, a SQLException will be thrown. Lowest
     * acceptable connection timeout is 250 ms. Default: 30000 (30 seconds)
     * 
     * @param connectionTimeoutMs - the milliseconds to wait for a connection from
     *                            the pool.
     */
    void setConnectionTimeout(long connectionTimeoutMs);

    /**
     * This property controls the maximum number of milliseconds that a client
     * will wait for a connection from the pool. If this time is exceeded without a
     * connection becoming available, a SQLException will be thrown. Lowest
     * acceptable connection timeout is 250 ms. Default: 30000 (30 seconds)
     * 
     * @return the milliseconds to wait for a connection from
     *         the pool.
     */
    long getConnectionTimeout();

    /**
     * This property controls the maximum amount of time that a connection will be
     * tested for aliveness. This value must be less than the connectionTimeout.
     * Lowest acceptable validation timeout is 250 ms. Default: 5000
     * 
     * @param validationTimeoutMs - the milliseconds that a connection will be
     *                            tested for aliveness.
     */
    void setValidationTimeout(long validationTimeoutMs);

    /**
     * This property controls the maximum amount of time that a connection will be
     * tested for aliveness. This value must be less than the connectionTimeout.
     * Lowest acceptable validation timeout is 250 ms. Default: 5000
     * 
     * @return the milliseconds that a connection will be tested for aliveness.
     */
    long getValidationTimeout();

    /**
     * 
     * This property controls the maximum amount of time that a connection is
     * allowed to sit idle in the pool. This setting only applies when minimumIdle
     * is defined to be less than maximumPoolSize. Idle connections will not be
     * retired once the pool reaches minimumIdle connections. Whether a connection
     * is retired as idle or not is subject to a maximum variation of +30 seconds,
     * and average variation of +15 seconds. A connection will never be retired as
     * idle before this timeout. A value of 0 means that idle connections are never
     * removed from the pool. The minimum allowed value is 10000ms (10 seconds).
     * Default: 600000 (10 minutes)
     * 
     * @param idleTimeoutMs - the maximum milliseconds that a connection is allowed
     *                      to sit idle in the pool.
     */
    void setIdleTimeout(long idleTimeoutMs);

    /**
     * This property controls the maximum amount of time that a connection is
     * allowed to sit idle in the pool. This setting only applies when minimumIdle
     * is defined to be less than maximumPoolSize. Idle connections will not be
     * retired once the pool reaches minimumIdle connections. Whether a connection
     * is retired as idle or not is subject to a maximum variation of +30 seconds,
     * and average variation of +15 seconds. A connection will never be retired as
     * idle before this timeout. A value of 0 means that idle connections are never
     * removed from the pool. The minimum allowed value is 10000ms (10 seconds).
     * Default: 600000 (10 minutes)
     * 
     * @return the maximum milliseconds that a connection is allowed to sit idle in
     *         the pool.
     */
    long getIdleTimeout();

    /**
     * This property controls the minimum number of idle connections that HikariCP
     * tries to maintain in the pool. If the idle connections dip below this value
     * and total connections in the pool are less than maximumPoolSize, HikariCP
     * will make a best effort to add additional connections quickly and
     * efficiently. However, for maximum performance and responsiveness to spike
     * demands, we recommend not setting this value and instead allowing HikariCP to
     * act as a fixed size connection pool. Default: same as maximumPoolSize
     * 
     * @param minIdle - the minimum number of idle connection in the pool.
     */
    void setMinIdle(int minIdle);

    /**
     * This property controls the minimum number of idle connections that HikariCP
     * tries to maintain in the pool. If the idle connections dip below this value
     * and total connections in the pool are less than maximumPoolSize, HikariCP
     * will make a best effort to add additional connections quickly and
     * efficiently. However, for maximum performance and responsiveness to spike
     * demands, we recommend not setting this value and instead allowing HikariCP to
     * act as a fixed size connection pool. Default: same as maximumPoolSize
     * 
     * @return the minimum number of idle connection in the pool.
     */
    int getMinIdle();

    /**
     * This property controls the amount of time that a connection can be out of the
     * pool before a message is logged indicating a possible connection leak. A
     * value of 0 means leak detection is disabled. Lowest acceptable value for
     * enabling leak detection is 2000 (2 seconds). Default: 0
     * 
     * @param leakDetectionThresholdMs - the milliseconds that a connection can be
     *                                 out of the pool before a message is logged
     *                                 indicating a possible connection leak.
     */
    void setLeakDetectionThreshold(long leakDetectionThresholdMs);

    /**
     * This property controls the amount of time that a connection can be out of the
     * pool before a message is logged indicating a possible connection leak. A
     * value of 0 means leak detection is disabled. Lowest acceptable value for
     * enabling leak detection is 2000 (2 seconds). Default: 0
     * 
     * @return the milliseconds that a connection can be out of the pool before a
     *         message is logged indicating a possible connection leak.
     */
    long getLeakDetectionThreshold();

    /**
     * This property controls the maximum lifetime of a connection in the pool. An
     * in-use connection will never be retired, only when it is closed will it then
     * be removed. On a connection-by-connection basis, minor negative attenuation
     * is applied to avoid mass-extinction in the pool. We strongly recommend
     * setting this value, and it should be several seconds shorter than any
     * database or infrastructure imposed connection time limit. A value of 0
     * indicates no maximum lifetime (infinite lifetime), subject of course to the
     * idleTimeout setting. The minimum allowed value is 30000ms (30 seconds).
     * Default: 1800000 (30 minutes)
     * 
     * @param maxLifetimeMs - the maximum lifetime of a connection in the pool.
     */
    void setMaxLifetime(long maxLifetimeMs);

    /**
     * This property controls the maximum lifetime of a connection in the pool. An
     * in-use connection will never be retired, only when it is closed will it then
     * be removed. On a connection-by-connection basis, minor negative attenuation
     * is applied to avoid mass-extinction in the pool. We strongly recommend
     * setting this value, and it should be several seconds shorter than any
     * database or infrastructure imposed connection time limit. A value of 0
     * indicates no maximum lifetime (infinite lifetime), subject of course to the
     * idleTimeout setting. The minimum allowed value is 30000ms (30 seconds).
     * Default: 1800000 (30 minutes)
     * 
     * @return the maximum lifetime of a connection in the pool.
     */
    long getMaxLifetime();
}
