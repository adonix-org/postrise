package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.security.RoleSecuritySettings;

public interface DataSourceContext
        extends ConnectionPoolSettings, ConnectionPoolStatus, ConnectionSettingsReadable, RoleSecuritySettings {

    /**
     * @return A {@link Connection} to the data source.
     * @throws SQLException if a database error occurs.
     */
    Connection getConnection() throws SQLException;
}
