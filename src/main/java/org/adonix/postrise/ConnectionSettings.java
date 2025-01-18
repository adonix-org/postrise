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
 * Apply specific {@link ConnectionSettings} for the data source by
 * implementing the {@link ConfigurationListener} interface.
 * <p>
 * Most implementations will call {@link #setLoginRole(String)} and
 * {@link #setLoginPassword(String)} in the
 * {@link ConfigurationListener#onConfigure(ConnectionSettings)
 * onConfigure(ConnectionSettings)} method.
 */
public interface ConnectionSettings extends ConnectionPoolStatus {
    /**
     * Get the name of the database for the configuration.
     * 
     * @return the database name.
     */
    String getDatabaseName();

    /**
     * Set the JDBC Url for this connection.
     * 
     * @param url - a valid JDBC Url most often in the fomat
     *            jdbc:db://hostname:port/database
     */
    void setJdbcUrl(String url);

    /**
     * Set the JDBC Url using the provided host and port.
     * <p>
     * Implementations will typically require a database name which can be accessed
     * via the {@link #getDatabaseName()} method.
     * 
     * @param hostname - the database server hostname.
     * @param port     - the database server port.
     */
    void setJdbcUrl(String hostname, Integer port);

    /**
     * Get the current JDBC Url.
     * 
     * @return the JDBC Url for this data source.
     */
    String getJdbcUrl();

    void setLoginRole(String role);

    String getLoginRole();

    void setLoginPassword(String password);

    void setAutoCommit(boolean isAutoCommit);

    boolean isAutoCommit();

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

    Properties getDataSourceProperties();
}
