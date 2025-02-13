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

package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresContainer extends PostgresServer {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    protected final JdbcDatabaseContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    public static final int MAX_CONNECTIONS = 199;
    public static final String DB_NAME = "postrise";
    public static final String DB_USER = "postrise";
    public static final String DB_PASS = "postrise";

    PostgresContainer() {
        container
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USER)
                .withPassword(DB_PASS)
                .withCommand("postgres -c max_connections=" + MAX_CONNECTIONS);

        addListener(new PostriseListener());
    }

    @Override
    public String getHostName() {
        return container.getHost();
    }

    @Override
    public Integer getPort() {
        return container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
    }

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setAutoCommit(true);
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }

    public final void startContainer() {
        container.start();
        LOGGER.info("{} container started.", container.getDockerImageName());
    }

    public final void stopContainer() {
        container.stop();
        LOGGER.info("{} container stopped.", container.getDockerImageName());
    }

    public void logStatus() {
        LOGGER.info("{}: Connections Total={} Active={} Idle={}", this,
                getTotalConnections(),
                getActiveConnections(),
                getIdleConnections());
    }
}
