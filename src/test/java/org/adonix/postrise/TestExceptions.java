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
import org.adonix.postrise.servers.PostgresDocker;
import org.adonix.postrise.servers.TestDatabaseListener;
import org.adonix.postrise.servers.TestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

public class TestExceptions {

    private static final PostgresDocker server = new TestServer();

    @DisplayName("EMPTY Database Name")
    @Test
    void testEmptyDatabaseName() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(" ");
        });
        assertEquals(t.getMessage(), "Illegal EMPTY String for databaseName");
    }

    @DisplayName("NULL Database Name")
    @Test
    void testNullDatabaseName() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(null);
        });
        assertEquals(t.getMessage(), "Illegal NULL String for databaseName");
    }

    @DisplayName("EMPTY ROLE String")
    @Test
    void testEmptyRoleString() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(listener.getDatabaseName(), " ");
        });
        assertEquals(t.getMessage(), "Illegal EMPTY String for roleName");
    }

    @DisplayName("NULL ROLE String")
    @Test
    void testNullRoleString() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.getConnection(listener.getDatabaseName(), null);
        });
        assertEquals(t.getMessage(), "Illegal NULL String for roleName");
    }

    @DisplayName("NULL Listener")
    @Test
    void testNullListener() throws SQLException {
        final Throwable t = assertThrows(IllegalArgumentException.class, () -> {
            server.addListener(null);
        });
        assertEquals(t.getMessage(), "Illegal NULL Object for listener");
    }

    @DisplayName("NOLOGIN Exception")
    @Test
    void testNoLoginException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "no_login_no_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals(cause.getMessage(), "FATAL: role \"no_login_no_super\" is not permitted to log in");
    }

    @DisplayName("NOLOGIN SUPERUSER Exception")
    @Test
    void testNoLoginSuperUserException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "no_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof PSQLException);
        assertEquals(cause.getMessage(), "FATAL: role \"no_login_with_super\" is not permitted to log in");
    }

    @DisplayName("Default Security SUPERUSER LOGIN Exception")
    @Test
    void testDefaultSecuritySuperUserLoginException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_DEFAULT_ROLE_SECURITY,
                "with_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals(cause.getMessage(), "SECURITY: with_login_with_super is a SUPERUSER role");
    }

    @DisplayName("Strict Security LOGIN SUPERUSER Exception")
    @Test
    void testStrictSecurityLoginSuperUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_with_super");
        final Throwable t = assertThrows(CreateDataSourceException.class, () -> {
            server.getConnection(listener.getDatabaseName());
        });

        final Throwable cause = t.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RoleSecurityException);
        assertEquals("SECURITY: with_login_with_super is a SUPERUSER role", cause.getMessage());
    }

    @DisplayName("Strict Security SET ROLE SUPERUSER Exception")
    @Test
    void testStrictSecuritySetRoleSuperUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(listener.getDatabaseName(), "no_login_with_super");
        });
        assertEquals(t.getMessage(), "SECURITY: no_login_with_super is a SUPERUSER role");
    }

    @DisplayName("Strict Security SET ROLE LOGIN Exception")
    @Test
    void testStrictSecuritySetRoleLoginUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(listener.getDatabaseName(), "with_login_no_super");
        });
        assertEquals(t.getMessage(), "SECURITY: with_login_no_super is a LOGIN role");
    }

    @DisplayName("Strict Security SET ROLE SUPERUSER LOGIN Exception")
    @Test
    void testStrictSecuritySetRoleSuperLoginUser() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, POSTGRES_STRICT_ROLE_SECURITY,
                "with_login_no_super");
        final Throwable t = assertThrows(RoleSecurityException.class, () -> {
            server.getConnection(listener.getDatabaseName(), "with_login_with_super");
        });
        assertEquals(t.getMessage(), "SECURITY: with_login_with_super is a SUPERUSER role");
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
        assertEquals(cause.getMessage(), "FATAL: database \"not_a_database\" does not exist");
    }

    @DisplayName("ROLE Does Not Exist Exception")
    @Test
    void testRoleDoesNotExistException() throws SQLException {
        final DatabaseListener listener = new TestDatabaseListener(server, "with_login_no_super");
        try (final Connection connection = server.getConnection(listener.getDatabaseName())) {
            final Throwable t = assertThrows(RoleSecurityException.class, () -> {
                PostgresRoleDAO.getRole(connection, "role_does_not_exist");
            });
            assertEquals(t.getMessage(), "SECURITY: role 'role_does_not_exist' does not exist");
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
