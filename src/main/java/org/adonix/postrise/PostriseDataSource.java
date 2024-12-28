package org.adonix.postrise;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariDataSource;

abstract class PostriseDataSource implements ConnectionProvider {

    private final HikariDataSource dataSource = new HikariDataSource();

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void setJdbcUrl(final String url) {
        dataSource.setJdbcUrl(url);
    }

    @Override
    public String getJdbcUrl() {
        return dataSource.getJdbcUrl();
    }

    @Override
    public void setDriverClassName(final String driverClassName) {
        dataSource.setDriverClassName(driverClassName);
    }

    @Override
    public String getDriverClassName() {
        return dataSource.getDriverClassName();
    }

    @Override
    public void setUsername(final String username) {
        dataSource.setUsername(username);
    }

    @Override
    public String getUsername() {
        return dataSource.getUsername();
    }

    @Override
    public void setPassword(final String password) {
        dataSource.setPassword(password);
    }

    @Override
    public String getPassword() {
        return dataSource.getPassword();
    }

    @Override
    public void setMaxPoolSize(final int size) {
        dataSource.setMaximumPoolSize(size);
    }

    @Override
    public int getMaxPoolSize() {
        return dataSource.getMaximumPoolSize();
    }

    @Override
    public void setConnectionTimeout(final long connectionTimeoutMs) {
        dataSource.setConnectionTimeout(connectionTimeoutMs);
    }

    @Override
    public long getConnectionTimeout() {
        return dataSource.getConnectionTimeout();
    }

    @Override
    public void setIdleTimeout(final long idleTimeoutMs) {
        dataSource.setIdleTimeout(idleTimeoutMs);
    }

    @Override
    public long getIdleTimeout() {
        return dataSource.getIdleTimeout();
    }

    @Override
    public void setMinIdle(final int minIdle) {
        dataSource.setMinimumIdle(minIdle);
    }

    @Override
    public int getMinIdle() {
        return dataSource.getMinimumIdle();
    }

    @Override
    public void setAutoCommit(final boolean isAutoCommit) {
        dataSource.setAutoCommit(isAutoCommit);
    }

    @Override
    public boolean isAutoCommit() {
        return dataSource.isAutoCommit();
    }

    @Override
    public void setValidationTimeout(final long validationTimeoutMs) {
        dataSource.setValidationTimeout(validationTimeoutMs);
    }

    @Override
    public long getValidationTimeout() {
        return dataSource.getValidationTimeout();
    }

    @Override
    public void setLeakDetectionThreshold(final long leakDetectionThresholdMs) {
        dataSource.setLeakDetectionThreshold(leakDetectionThresholdMs);
    }

    @Override
    public long getLeakDetectionThreshold() {
        return dataSource.getLeakDetectionThreshold();
    }

    @Override
    public void setMaxLifetime(final long maxLifetimeMs) {
        dataSource.setMaxLifetime(maxLifetimeMs);
    }

    @Override
    public long getMaxLifetime() {
        return dataSource.getMaxLifetime();
    }

    @Override
    public void setConnectionTestQuery(final String sql) {
        dataSource.setConnectionTestQuery(sql);
    }

    @Override
    public String getConnectionTestQuery() {
        return dataSource.getConnectionTestQuery();
    }

    @Override
    public void addDataSourceProperty(String propertyName, Object value) {
        dataSource.addDataSourceProperty(propertyName, value);
    }

    @Override
    public int getActiveConnections() {
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }

    @Override
    public int getIdleConnections() {
        return dataSource.getHikariPoolMXBean().getIdleConnections();
    }

    @Override
    public int getTotalConnections() {
        return dataSource.getHikariPoolMXBean().getTotalConnections();
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
