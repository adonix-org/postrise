package org.adonix.postrise;

public interface ConnectionSettings extends ConnectionPoolSettings {

    void setJdbcUrl(String url);

    String getJdbcUrl();

    void setDriverClassName(String driverClassName);

    String getDriverClassName();

    void setUsername(String username);

    String getUsername();

    void setPassword(String password);

    String getPassword();

    void setAutoCommit(boolean isAutoCommit);

    boolean isAutoCommit();

    void setConnectionTestQuery(String sql);

    String getConnectionTestQuery();

    void addDataSourceProperty(String propertyName, Object value);
}
