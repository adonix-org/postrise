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
 * Writable settings for a data source before it is created. After the data
 * source is created, these settings become read-only.
 * 
 * @see ConnectionSettingsRead
 * @see DataSourceSettings
 */
interface ConnectionSettingsWrite extends ConnectionSettingsRead {

    /**
     * Set the {@code LOGIN} username for connecting to the data source.
     * 
     * @param username - the data source {@code LOGIN} username.
     */
    void setUsername(String username);

    /**
     * Set the {@code LOGIN} password for connecting to the data source.
     * 
     * @param password - the data source {@code LOGIN} password.
     */
    void setPassword(String password);

    /**
     * This property controls the default auto-commit behavior of connections
     * returned from the pool. It is a boolean value. Default: true
     * 
     * @param isAutoCommit - default auto commit setting for new data sources.
     */
    void setAutoCommit(boolean isAutoCommit);

    /**
     * Add any properties for the data source configuration.
     * 
     * @param propertyName - data source property name.
     * @param value        - data source property value.
     */
    void addDataSourceProperty(String propertyName, Object value);
}
