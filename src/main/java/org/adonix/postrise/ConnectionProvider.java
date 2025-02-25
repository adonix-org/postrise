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

import java.sql.Connection;

/**
 * Implementations of this interface will return a JDBC {@link Connection}
 * using {@link DataSourceSettings} configured in the
 * {@link DataSourceListener#beforeCreate(DataSourceSettings) beforeCreate()}
 * event.
 */
interface ConnectionProvider extends AutoCloseable, DataSourceContext, DataSourceSettings {
    /**
     * Closes this {@link ConnectionProvider}. Closing all data sources is handled
     * by the {@link Server} implementation.
     */
    void close();
}
