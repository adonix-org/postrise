package org.adonix.postrise.servers;

import java.sql.Connection;
import java.sql.SQLException;

import org.adonix.postrise.PostgresDataSource;

public class SetRoleDataSource extends PostgresDataSource {

    protected SetRoleDataSource(String databaseName) {
        super(databaseName);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}
