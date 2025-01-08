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
 * implementing the {@link DataSourceListener} interface.
 * <p>
 * Most implementations will call {@link #setUsername(String)} and
 * {@link #setPassword(String)} from the
 * {@link DataSourceListener#onConfigure(ConnectionSettings)} method.
 */
public interface ConnectionSettings extends ConnectionPoolSettings {

    /**
     * When a new data source is created, this method will return the name of the
     * database that will be the target of the connection.
     * 
     * @return the database for this data source.
     */
    String getDatabase();

    /**
     * Set the JDBC Url for this connection.
     * 
     * @param url a valid JDBC Url most often in the fomat
     *            jdbc:db://hostname:port/database
     */
    void setJdbcUrl(String url);

    /**
     * This method will return the current JDBC Url.
     * 
     * @return the JDBC Url for this data source.
     */
    String getJdbcUrl();

    /**
     * Implementations must return a valid JDBC Url with the given {@link Server}.
     * 
     * @param server the server providing a host and port to create a connection to
     *               the database.
     * @return a valid JDBC Url for this data source.
     * @see Server
     */
    String getJdbcUrl(final Server server);

    /**
     * Implementations must return a valid JDBC Url with the given hostname.
     * 
     * @param hostname the hostname to the database.
     * @return a valid JDBC Url for this data source.
     */
    String getJdbcUrl(final String hostname);

    /**
     * Implementations must return a valid JDBC Url with the given hostname and
     * port.
     * 
     * @param hostname the database server hostname.
     * @param port     the database server port.
     * @return a valid JDBC Url for this data source.
     */
    String getJdbcUrl(final String hostname, final Integer port);

    void setDriverClassName(String driverClassName);

    String getDriverClassName();

    void setUsername(String username);

    String getUsername();

    void setPassword(String password);

    String getPassword();

    void setAutoCommit(boolean isAutoCommit);

    boolean isAutoCommit();

    /**
     * Set the SQL query to be executed to test the validity of connections. Using
     * the JDBC4 <code>Connection.isValid()</code> method to test connection
     * validity can be more efficient on some databases and is recommended.
     *
     * @param sql a SQL query string
     */
    void setConnectionTestQuery(String sql);

    /**
     * Get the SQL query to be executed to test the validity of connections.
     *
     * @return the SQL query string, or null
     */
    String getConnectionTestQuery();

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
