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

import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_DEFAULT_ROLE_SECURITY;
import static org.adonix.postrise.security.RoleSecurityProviders.POSTGRES_STRICT_ROLE_SECURITY;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.security.PostgresRoleDAO;
import org.adonix.postrise.security.RoleSecurityException;
import org.adonix.postrise.servers.PostgresContainer;
import org.adonix.postrise.servers.TestDatabaseListener;
import org.adonix.postrise.servers.TestServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.postgresql.util.PSQLException;

class TestExceptions {

    private static final PostgresContainer server = new TestServer();

    private static final Logger LOGGER = LogManager.getLogger(TestExceptions.class);

    @DisplayName("EMPTY Database Name")
    @Test
    void testEmptyDatabaseName() {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(" ");
        });
        assertEquals("Illegal EMPTY String for databaseName", t.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("NULL Database Name")
    @Test
    void testNullDatabaseName() {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(null);
        });
        assertEquals("Illegal NULL String for databaseName", t.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("EMPTY ROLE String")
    @Test
    void testEmptyRoleString() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, "with_login_no_super").getDatabaseName();
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(databaseName, " ");
        });
        assertEquals("Illegal EMPTY String for roleName", t.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("NULL ROLE String")
    @Test
    void testNullRoleString() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, "with_login_no_super").getDatabaseName();
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(databaseName, null);
        });
        assertEquals("Illegal NULL String for roleName", t.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("NULL Listener")
    @Test
    void testNullListener() {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.addListener(null);
        });
        assertEquals("Illegal NULL Object for listener", t.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("NOLOGIN Exception")
    @Test
    void testNoLoginException() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, "no_login_no_super").getDatabaseName();
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(databaseName);
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: role \"no_login_no_super\" is not permitted to log in", cause.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("NOLOGIN SUPERUSER Exception")
    @Test
    void testNoLoginSuperUserException() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, "no_login_with_super").getDatabaseName();
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(databaseName);
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: role \"no_login_with_super\" is not permitted to log in", cause.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("Default Security SUPERUSER LOGIN Exception")
    @Test
    void testDefaultSecuritySuperUserLoginException() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_with_super").getDatabaseName();
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(databaseName);
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: \"with_login_with_super\" is a SUPERUSER role", cause.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("Strict Security LOGIN SUPERUSER Exception")
    @Test
    void testStrictSecurityLoginSuperUser() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_with_super").getDatabaseName();
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(databaseName);
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: \"with_login_with_super\" is a SUPERUSER role", cause.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("Strict Security SET ROLE Exception Tests")
    @ParameterizedTest(name = "[{index}] Role: {1}")
    @CsvSource({
            "no_login_with_super, SECURITY: \"no_login_with_super\" is a SUPERUSER role",
            "with_login_no_super, SECURITY: \"with_login_no_super\" is a LOGIN role",
            "with_login_with_super, SECURITY: \"with_login_with_super\" is a SUPERUSER role"
    })
    void testStrictSecuritySetRoleException(String role, String expectedMessage) throws SQLException {
        final String databaseName = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super").getDatabaseName();

        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(databaseName, role);
        });

        assertEquals(expectedMessage, t.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("Postgres Exception Propagation")
    @Test
    void testPostgresExceptionPropagation() {
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection("not_a_database");
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals("FATAL: database \"not_a_database\" does not exist", cause.getMessage());
        LOGGER.error("{}: {}", server, t);
    }

    @DisplayName("ROLE Does Not Exist Exception")
    @Test
    void testRoleDoesNotExistException() throws SQLException {
        final String databaseName = new TestDatabaseListener(server, "with_login_no_super").getDatabaseName();
        try (final Connection connection = server.getConnection(databaseName)) {
            final Throwable t = assertThrows(RoleSecurityException.class, () -> {
                PostgresRoleDAO.getRole(connection, "role_does_not_exist");
            });
            assertEquals("SECURITY: role \"role_does_not_exist\" does not exist", t.getMessage());
            LOGGER.error("{}: {}", server, t);
        }
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        server.startContainer();
        TestEnvironment.initialize(server);
    }

    @AfterAll
    static void afterAll() {
        server.close();
        server.stopContainer();
    }
}
