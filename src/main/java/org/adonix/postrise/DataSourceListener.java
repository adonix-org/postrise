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

import java.util.EventListener;

/**
 * Implementations of this interface will receive events during the lifecycle of
 * a data source.
 * 
 * @see PostriseServer#addListener(DataSourceListener)
 */
public interface DataSourceListener extends EventListener {

    /**
     * Configure the data source the first time a {@link java.sql.Connection
     * Connection} is requested.
     * 
     * @param settings - each new data source will be configured with these
     *                 {@link DataSourceSettings}.
     */
    default void beforeCreate(DataSourceSettings settings) {
    }

    /**
     * Subscribers to this event will be notified after a new
     * {@link DataSourceContext} is created.
     * 
     * @param context - the new data source.
     */
    default void afterCreate(DataSourceContext context) {
    }

    /**
     * Subscribers to this event will be notified before a
     * {@link DataSourceContext} is closed.
     * 
     * @param context - the data source that is closing.
     */
    default void beforeClose(DataSourceContext context) {
    }

    /**
     * Subscribers to this event will be notified after a
     * {@link DataSourceContext} is closed.
     * 
     * @param context - the data source that is closed.
     */
    default void afterClose(DataSourceContext context) {
    }
}
