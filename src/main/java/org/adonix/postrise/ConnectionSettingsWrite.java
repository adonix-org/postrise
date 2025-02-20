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

import java.util.Properties;
import javax.sql.DataSource;

/**
 * This interface provides configuration parameters for a connection to the
 * database.
 * 
 * Apply specific {@link DataSourceSettings} for the data source by
 * implementing the {@link DataSourceListener} interface.
 * <p>
 * Most implementations will call {@link #setUsername(String)} and
 * {@link #setPassword(String)} in the
 * {@link DataSourceListener#beforeCreate(DataSourceSettings)
 * beforeCreate(DataSourceSettings)} method.
 */
interface ConnectionSettingsWrite extends ConnectionSettingsRead {

    void setUsername(String username);

    void setPassword(String password);

    void setAutoCommit(boolean isAutoCommit);

    /**
     * Add a property (name/value pair) that will be used to configure the
     * {@link DataSource}/{@link java.sql.Driver}.
     * <p>
     * In the case of a {@link DataSource}, the property names will be translated to
     * Java setters following the Java Bean
     * naming convention. For example, the property {@code cachePrepStmts} will
     * translate into {@code setCachePrepStmts()} with the {@code value} passed as a
     * parameter.
     * <p>
     * In the case of a {@link java.sql.Driver}, the property will be added to a
     * {@link Properties} instance that will be passed to the driver during
     * {@link java.sql.Driver#connect(String, Properties)} calls.
     *
     * @param propertyName the name of the property
     * @param value        the value to be used by the DataSource/Driver
     */
    void addDataSourceProperty(String propertyName, Object value);
}
