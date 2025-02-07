package org.adonix.postrise.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EventListener;
import org.adonix.postrise.DataSourceContext;

public interface RoleSecurityListener extends EventListener {

    void onLogin(DataSourceContext context, Connection connection) throws SQLException;

}
