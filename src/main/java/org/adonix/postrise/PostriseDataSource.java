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

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariDataSource;

abstract class PostriseDataSource implements ConnectionProvider {

    private final HikariDataSource delegate = new HikariDataSource();

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public void setJdbcUrl(final String url) {
        delegate.setJdbcUrl(url);
    }

    @Override
    public String getJdbcUrl() {
        return delegate.getJdbcUrl();
    }

    @Override
    public void setDriverClassName(final String driverClassName) {
        delegate.setDriverClassName(driverClassName);
    }

    @Override
    public String getDriverClassName() {
        return delegate.getDriverClassName();
    }

    @Override
    public void setUsername(final String username) {
        delegate.setUsername(username);
    }

    @Override
    public String getUsername() {
        return delegate.getUsername();
    }

    @Override
    public void setPassword(final String password) {
        delegate.setPassword(password);
    }

    @Override
    public String getPassword() {
        return delegate.getPassword();
    }

    @Override
    public void setMaxPoolSize(final int size) {
        delegate.setMaximumPoolSize(size);
    }

    @Override
    public int getMaxPoolSize() {
        return delegate.getMaximumPoolSize();
    }

    @Override
    public void setConnectionTimeout(final long connectionTimeoutMs) {
        delegate.setConnectionTimeout(connectionTimeoutMs);
    }

    @Override
    public long getConnectionTimeout() {
        return delegate.getConnectionTimeout();
    }

    @Override
    public void setIdleTimeout(final long idleTimeoutMs) {
        delegate.setIdleTimeout(idleTimeoutMs);
    }

    @Override
    public long getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    @Override
    public void setMinIdle(final int minIdle) {
        delegate.setMinimumIdle(minIdle);
    }

    @Override
    public int getMinIdle() {
        return delegate.getMinimumIdle();
    }

    @Override
    public void setAutoCommit(final boolean isAutoCommit) {
        delegate.setAutoCommit(isAutoCommit);
    }

    @Override
    public boolean isAutoCommit() {
        return delegate.isAutoCommit();
    }

    @Override
    public void setValidationTimeout(final long validationTimeoutMs) {
        delegate.setValidationTimeout(validationTimeoutMs);
    }

    @Override
    public long getValidationTimeout() {
        return delegate.getValidationTimeout();
    }

    @Override
    public void setLeakDetectionThreshold(final long leakDetectionThresholdMs) {
        delegate.setLeakDetectionThreshold(leakDetectionThresholdMs);
    }

    @Override
    public long getLeakDetectionThreshold() {
        return delegate.getLeakDetectionThreshold();
    }

    @Override
    public void setMaxLifetime(final long maxLifetimeMs) {
        delegate.setMaxLifetime(maxLifetimeMs);
    }

    @Override
    public long getMaxLifetime() {
        return delegate.getMaxLifetime();
    }

    @Override
    public void setConnectionTestQuery(final String sql) {
        delegate.setConnectionTestQuery(sql);
    }

    @Override
    public String getConnectionTestQuery() {
        return delegate.getConnectionTestQuery();
    }

    @Override
    public void addDataSourceProperty(String propertyName, Object value) {
        delegate.addDataSourceProperty(propertyName, value);
    }

    @Override
    public int getActiveConnections() {
        return delegate.getHikariPoolMXBean().getActiveConnections();
    }

    @Override
    public int getIdleConnections() {
        return delegate.getHikariPoolMXBean().getIdleConnections();
    }

    @Override
    public int getTotalConnections() {
        return delegate.getHikariPoolMXBean().getTotalConnections();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
