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

import java.sql.Connection;
import org.adonix.postrise.servers.Servers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class PostriseTest extends TestEnvironment {

    private static final Logger LOGGER = LogManager.getLogger();

    @DisplayName("Run")
    @Test
    public void run() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run");
        }
    }

    @DisplayName("Run2")
    @Test
    public void run2() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run2");
        }
    }

    @DisplayName("Run3")
    @Test
    public void run3() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
            LOGGER.info("run3");
        }
    }
}
