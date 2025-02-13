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
import java.sql.Statement;
import org.adonix.postrise.servers.PostgresDocker;

abstract class TestEnvironment {

    private static final String[] SQL_FILES = { "roles.sql" };

    public static void initialize(final Server server) throws Exception {
        try (final Connection connection = server.getConnection(PostgresDocker.DB_NAME)) {
            for (final String fileName : SQL_FILES) {
                executeSqlFile(connection, fileName);
            }
        }
    }

    public static void executeSqlFile(final Connection connection, final String fileName) throws Exception {
        final String sql = Files.readString(
                Paths.get(TestEnvironment.class.getClassLoader().getResource(fileName).toURI()));
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
