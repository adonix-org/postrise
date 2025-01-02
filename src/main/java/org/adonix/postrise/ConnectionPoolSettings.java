/*
 * Copyright (C) 2024, 2025 Ty Busby
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

    void close();
}
