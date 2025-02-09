package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceContext
        extends ConnectionPoolSettings, ConnectionPoolStatus, ConnectionSettingsReadable {

    /**
     * @return A {@link Connection} to the data source.
     * @throws SQLException if a database error occurs.
     */
    Connection getConnection() throws SQLException;

    /**
     * Set the role to the roleName on the {@link Connection}.
     * 
     * @param roleName - The ROLE to be set for the connection.
     * @return A {@link Connection} to the data source.
     * @throws SQLException if a database error occurs.
     */
    Connection getConnection(String roleName) throws SQLException;
}
