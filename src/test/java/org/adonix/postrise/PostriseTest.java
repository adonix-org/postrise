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
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class PostriseTest {

    @Before
    public void before() {
        PostgresTestServer.start();
    }

    @Test
    public void run() throws Exception {
        try (final Server server = Servers.getLocalhostSuper();
                final Connection connection = server.getConnection("test", "test")) {
            connection.setAutoCommit(false);
        }
    }

    @After
    public void after() {
        PostgresTestServer.stop();
    }
}
