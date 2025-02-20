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

import java.sql.SQLException;
import java.util.EventListener;

/**
 * Implementations will receive the {@link #beforeCreate(DataSourceSettings)}
 * event to configure {@link DataSourceSettings} when a new data source is
 * created.
 * 
 * @see PostriseServer#addListener(DataSourceListener)
 */
public interface DataSourceListener extends EventListener {

    /**
     * @param settings - the {@link DataSourceSettings} applied when creating a new
     *                 {@link ConnectionProvider} instance.
     */
    default void beforeCreate(DataSourceSettings settings) throws SQLException {
    }

    default void afterCreate(DataSourceContext context) {
    }

    default void beforeClose(DataSourceContext context) {
    }

    default void afterClose(DataSourceContext context) {
    }
}
