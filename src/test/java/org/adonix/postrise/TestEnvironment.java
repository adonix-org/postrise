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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import org.adonix.postrise.servers.Servers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

public class TestEnvironment {

    protected static final Server LOCALHOST_SUPER = Servers.getLocalhostSuper();
    protected static final Server LOCALHOST = Servers.getLocalhost();

    @BeforeAll
    public static void beforeAll() throws Exception {
        PostgresTestServer.start();
        initialze();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        LOCALHOST.close();
        LOCALHOST_SUPER.close();
        PostgresTestServer.stop();
    }

    public static void initialze() throws Exception {
        try (final Connection connection = LOCALHOST_SUPER.getConnection("test", "test")) {
            executeFile(connection, "initialize.sql");
        }
    }

    public static void executeFile(final Connection connection, final String fileName) throws Exception {
        String str = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        connection.prepareStatement(str).executeUpdate();
    }
}
