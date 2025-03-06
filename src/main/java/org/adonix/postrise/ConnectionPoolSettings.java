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
 * Settings for a connection pool.
 */
interface ConnectionPoolSettings {

    /**
     * @param size - the maximum number of connections in the pool.
     */
    void setMaxPoolSize(int size);

    /**
     * @return the maximum number of connections in the pool.
     */
    int getMaxPoolSize();

    /**
     * @param connectionTimeoutMs - the milliseconds to wait for a connection from
     *                            the pool.
     */
    void setConnectionTimeout(long connectionTimeoutMs);

    /**
     * @return the milliseconds to wait for a connection from
     *         the pool.
     */
    long getConnectionTimeout();

    /**
     * @param validationTimeoutMs - the milliseconds that a connection will be
     *                            tested for aliveness.
     */
    void setValidationTimeout(long validationTimeoutMs);

    /**
     * @return the milliseconds that a connection will be tested for aliveness.
     */
    long getValidationTimeout();

    /**
     * @param idleTimeoutMs - the maximum milliseconds that a connection is allowed
     *                      to sit idle in the pool.
     */
    void setIdleTimeout(long idleTimeoutMs);

    /**
     * @return the maximum milliseconds that a connection is allowed to sit idle in
     *         the pool.
     */
    long getIdleTimeout();

    /**
     * @param minIdle - the minimum number of idle connection in the pool.
     */
    void setMinIdle(int minIdle);

    /**
     * @return the minimum number of idle connection in the pool.
     */
    int getMinIdle();

    /**
     * @param leakDetectionThresholdMs - the milliseconds that a connection can be
     *                                 out of the pool before a message is logged
     *                                 indicating a possible connection leak.
     */
    void setLeakDetectionThreshold(long leakDetectionThresholdMs);

    /**
     * @return the milliseconds that a connection can be out of the pool before a
     *         message is logged indicating a possible connection leak.
     */
    long getLeakDetectionThreshold();

    /**
     * @param maxLifetimeMs - the maximum lifetime of a connection in the pool.
     */
    void setMaxLifetime(long maxLifetimeMs);

    /**
     * @return the maximum lifetime of a connection in the pool.
     */
    long getMaxLifetime();
}
