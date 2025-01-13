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

public class PostgresDockerContainer extends PostgreSQLContainer<PostgresDockerContainer>
        implements ConfigurationListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    public PostgresDockerContainer() {
        super(POSTGRES_IMAGE_NAME);
    }

    @Override
    public void start() {
        LOGGER.info("Starting container {}...", getDockerImageName());
        super.start();
        LOGGER.info("Container {} started", getDockerImageName());
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping container {}...", getDockerImageName());
        super.stop();
        LOGGER.info("Container {} stopped", getDockerImageName());
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(withDatabaseName(settings.getDatabaseName()).getJdbcUrl());
    }
}
