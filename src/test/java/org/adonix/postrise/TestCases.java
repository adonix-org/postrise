package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class TestCases extends TestEnvironment {

    @Test
    void run1() throws SQLException {
        try (final Connection connection = ALPHA.getConnection("postgres", "admin")) {
            connection.getMetaData();
        }
    }

    @Test
    void run2() throws SQLException {
        try (final Connection connection = BETA.getConnection("postgres", "beta_application")) {
            connection.getAutoCommit();
        }
    }
}
