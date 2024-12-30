/*
 * Copyright (C) 2024 Ty Busby
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

package org.adonix.postrise.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.adonix.postrise.Server;
import org.adonix.postrise.servers.Servers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSecurity {

    private static final Logger LOGGER = LogManager.getLogger();

    @Before
    public void start() {
        LOGGER.info("Before");
    }

    // Test for error creating datasource

    // Test for logging in as superuser

    // Test for login user same as connection role

    // Test for connection role is not superuser or login user

    @Test
    public void run() throws Exception {
        try (final Server server = Servers.getLocalhost();
                final Connection connection = server.getConnection("adonix", "adonix_test");
                final Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery("SELECT session_user, current_user")) {

            assertNotNull(connection);

            assertTrue(rs.next());

            LOGGER.info("{} {}", rs.getString(1), rs.getString(2));
        }
    }

    @After
    public void end() {
        LOGGER.info("After");
    }
}
