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

/**
 * Read-only settings for a data source after it is created.
 * 
 * @see DataSourceContext
 */
interface ConnectionSettingsRead extends DatabaseNameProvider {

    /**
     * @return the JDBC Url for this data source.
     */
    String getJdbcUrl();

    /**
     * @return the {@code LOGIN} username.
     */
    String getUsername();

    /**
     * @return the default auto-commit setting for this data source.
     */
    boolean isAutoCommit();

    /**
     * @return the {@link Properties} for this data source.
     */
    Properties getDataSourceProperties();
}
