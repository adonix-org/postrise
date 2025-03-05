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
 * Provides a snapshot of the connection pool. All values are transient and
 * depend on the underlying implementation which represents a point-in-time
 * measurement.
 * <p>
 * Therefore, the TOTAL connections could be greater or less than the SUM of the
 * ACTIVE and IDLE connections when queried.
 */
interface ConnectionPoolStatus {

    /**
     * @return the current number of active (in-use) connections in the pool.
     */
    int getActiveConnections();

    /**
     * @return the current number of idle connections in the pool.
     */
    int getIdleConnections();

    /**
     * @return the total number of connections in the pool.
     */
    int getTotalConnections();

    /**
     * @return the number of threads awaiting a connection from the pool.
     */
    int getThreadsAwaitingConnection();
}
