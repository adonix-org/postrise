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

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

abstract class PostriseDataSource implements ConnectionProvider {

    private final HikariDataSource delegate;
    private final HikariConfigMXBean config;
    private final String databaseName;

    PostriseDataSource(final String databaseName) {
        this.databaseName = databaseName;
        this.delegate = new HikariDataSource();
        this.config = this.delegate;
    }

    @Override
    public final String getDatabaseName() {
        return databaseName;
    }

    @Override
    public final Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public final String getJdbcUrl() {
        return delegate.getJdbcUrl();
    }

    @Override
    public final void setJdbcUrl(final String url) {
        delegate.setJdbcUrl(url);
    }

    @Override
    public final String getLoginRole() {
        return delegate.getUsername();
    }

    @Override
    public final void setLoginRole(final String username) {
        delegate.setUsername(username);
    }

    @Override
    public final void setLoginPassword(final String password) {
        delegate.setPassword(password);
    }

    @Override
    public final int getMaxPoolSize() {
        return config.getMaximumPoolSize();
    }

    @Override
    public final void setMaxPoolSize(final int size) {
        config.setMaximumPoolSize(size);
    }

    @Override
    public final long getConnectionTimeout() {
        return config.getConnectionTimeout();
    }

    @Override
    public final void setConnectionTimeout(final long connectionTimeoutMs) {
        config.setConnectionTimeout(connectionTimeoutMs);
    }

    @Override
    public final long getIdleTimeout() {
        return config.getIdleTimeout();
    }

    @Override
    public final void setIdleTimeout(final long idleTimeoutMs) {
        config.setIdleTimeout(idleTimeoutMs);
    }

    @Override
    public final int getMinIdle() {
        return config.getMinimumIdle();
    }

    @Override
    public final void setMinIdle(final int minIdle) {
        config.setMinimumIdle(minIdle);
    }

    @Override
    public final boolean isAutoCommit() {
        return delegate.isAutoCommit();
    }

    @Override
    public final void setAutoCommit(final boolean isAutoCommit) {
        delegate.setAutoCommit(isAutoCommit);
    }

    @Override
    public final long getValidationTimeout() {
        return config.getValidationTimeout();
    }

    @Override
    public final void setValidationTimeout(final long validationTimeoutMs) {
        config.setValidationTimeout(validationTimeoutMs);
    }

    @Override
    public final long getLeakDetectionThreshold() {
        return config.getLeakDetectionThreshold();
    }

    @Override
    public final void setLeakDetectionThreshold(final long leakDetectionThresholdMs) {
        config.setLeakDetectionThreshold(leakDetectionThresholdMs);
    }

    @Override
    public final long getMaxLifetime() {
        return config.getMaxLifetime();
    }

    @Override
    public final void setMaxLifetime(final long maxLifetimeMs) {
        config.setMaxLifetime(maxLifetimeMs);
    }

    @Override
    public final void addDataSourceProperty(String propertyName, Object value) {
        delegate.addDataSourceProperty(propertyName, value);
    }

    @Override
    public final Properties getDataSourceProperties() {
        return delegate.getDataSourceProperties();
    }

    @Override
    public final Optional<Integer> getActiveConnections() {
        return getPoolStatus(HikariPoolMXBean::getActiveConnections);
    }

    @Override
    public final Optional<Integer> getIdleConnections() {
        return getPoolStatus(HikariPoolMXBean::getIdleConnections);
    }

    @Override
    public final Optional<Integer> getTotalConnections() {
        return getPoolStatus(HikariPoolMXBean::getTotalConnections);
    }

    @Override
    public final Optional<Integer> getThreadsAwaitingConnection() {
        return getPoolStatus(HikariPoolMXBean::getThreadsAwaitingConnection);
    }

    private final Optional<Integer> getPoolStatus(Function<HikariPoolMXBean, Integer> method) {
        return Optional.ofNullable(delegate.getHikariPoolMXBean()).map(method);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public String toString() {
        return getLoginRole() + "@" + getJdbcUrl();
    }
}
