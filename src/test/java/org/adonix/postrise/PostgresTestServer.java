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
import org.testcontainers.containers.JdbcDatabaseContainer;

public class PostgresTestServer {

    private static final Logger LOGGER = LogManager.getLogger();

    protected static final JdbcDatabaseContainer<PostgresDockerContainer> container = new PostgresDockerContainer();

    public static void start() {
        LOGGER.info("Starting container {}...", container.getDockerImageName());
        container.start();
        LOGGER.info("Container {} started", container.getDockerImageName());
    }

    public static void stop() {
        LOGGER.info("Stopping container {}...", container.getDockerImageName());
        container.stop();
        LOGGER.info("Container {} stopped", container.getDockerImageName());
    }
}
