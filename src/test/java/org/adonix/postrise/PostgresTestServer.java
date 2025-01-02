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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresTestServer implements DataSourceListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final DockerImageName IMAGE_NAME = DockerImageName.parse("postgres:17");

    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(IMAGE_NAME);

    public void start() {
        LOGGER.info("Starting container {}...", IMAGE_NAME);
        container.start();
        LOGGER.info("Container {} started", IMAGE_NAME);
    }

    public void stop() {
        LOGGER.info("Stopping container {}...", IMAGE_NAME);
        container.stop();
        LOGGER.info("Container {} stopped", IMAGE_NAME);
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(container.getJdbcUrl());
        settings.setUsername(container.getUsername());
        settings.setPassword(container.getPassword());
    }
}
