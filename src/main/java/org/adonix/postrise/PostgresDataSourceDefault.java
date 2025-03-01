package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;
import org.adonix.postrise.security.PostgresRoleDAO;

final class PostgresDataSourceDefault extends PostgresDataSource {

    public PostgresDataSourceDefault(final Server server, final String databaseName) {
        super(server, databaseName);
    }

    @Override
    public Connection getConnection(final String roleName) throws SQLException {
        Guard.check("roleName", roleName);
        final Connection connection = super.getConnection();
        try {
            getRoleSecurity().onSetRole(this, connection, roleName);
            PostgresRoleDAO.setRole(connection, roleName);
            return connection;
        } catch (final Exception e) {
            connection.close();
            throw e;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection();
        try {
            resetRole(connection);
            return connection;
        } catch (final Exception e) {
            connection.close();
            throw e;
        }
    }

    /**
     * Any {@link Connection} returned to the pool retains previous {@code SET ROLE}
     * which could cause unexpected permission errors when the connection is
     * re-used. {@code RESET ROLE} when getting a {@link Connection} from the pool
     * unless a {@code ROLE} is provided via
     * {@link #getConnection(String roleName)}.
     * 
     * @see #getConnection()
     * @see <a href=
     *      "https://github.com/brettwooldridge/HikariCP/wiki/Pool-Analysis">HikariCP
     *      Pool Analysis</a>
     */
    protected void resetRole(final Connection connection) throws SQLException {
        PostgresRoleDAO.resetRole(connection);
    }
}
