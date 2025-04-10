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

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.ToIntFunction;
import org.adonix.postrise.security.RoleSecurityListener;

abstract class PostriseDataSource implements ConnectionProvider {

    private final HikariDataSource delegate;
    private final String databaseName;
    private RoleSecurityListener roleSecurity;

    /**
     * Subclass implementations should provide a default security setting.
     * 
     * @return the default {@link RoleSecurityListener} for this data source.
     */
    protected abstract RoleSecurityListener getDefaultRoleSecurity();

    abstract String getJdbcUrl(final Server server);

    PostriseDataSource(final Server server, final String databaseName) {
        this.databaseName = databaseName;
        this.delegate = new HikariDataSource();
        setJdbcUrl(server);
        setRoleSecurity(getDefaultRoleSecurity());
        setUsername(System.getProperty("user.name"));
    }

    private void setJdbcUrl(final Server server) {
        delegate.setJdbcUrl(getJdbcUrl(server));
    }

    @Override
    public final String getJdbcUrl() {
        return delegate.getJdbcUrl();
    }

    @Override
    public final RoleSecurityListener getRoleSecurity() {
        return roleSecurity;
    }

    @Override
    public final void setRoleSecurity(final RoleSecurityListener security) {
        this.roleSecurity = security;
    }

    @Override
    public final String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public final String getUsername() {
        return delegate.getUsername();
    }

    @Override
    public final void setUsername(final String username) {
        delegate.setUsername(username);
    }

    @Override
    public final void setPassword(final String password) {
        delegate.setPassword(password);
    }

    @Override
    public final int getMaxPoolSize() {
        return delegate.getMaximumPoolSize();
    }

    @Override
    public final void setMaxPoolSize(final int size) {
        delegate.setMaximumPoolSize(size);
    }

    @Override
    public final long getConnectionTimeout() {
        return delegate.getConnectionTimeout();
    }

    @Override
    public final void setConnectionTimeout(final long connectionTimeoutMs) {
        delegate.setConnectionTimeout(connectionTimeoutMs);
    }

    @Override
    public final long getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    @Override
    public final void setIdleTimeout(final long idleTimeoutMs) {
        delegate.setIdleTimeout(idleTimeoutMs);
    }

    @Override
    public final int getMinIdle() {
        return delegate.getMinimumIdle();
    }

    @Override
    public final void setMinIdle(final int minIdle) {
        delegate.setMinimumIdle(minIdle);
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
        return delegate.getValidationTimeout();
    }

    @Override
    public final void setValidationTimeout(final long validationTimeoutMs) {
        delegate.setValidationTimeout(validationTimeoutMs);
    }

    @Override
    public final long getLeakDetectionThreshold() {
        return delegate.getLeakDetectionThreshold();
    }

    @Override
    public final void setLeakDetectionThreshold(final long leakDetectionThresholdMs) {
        delegate.setLeakDetectionThreshold(leakDetectionThresholdMs);
    }

    @Override
    public final long getMaxLifetime() {
        return delegate.getMaxLifetime();
    }

    @Override
    public final void setMaxLifetime(final long maxLifetimeMs) {
        delegate.setMaxLifetime(maxLifetimeMs);
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
    public final int getActiveConnections() {
        return getPoolStatus(HikariPoolMXBean::getActiveConnections);
    }

    @Override
    public final int getIdleConnections() {
        return getPoolStatus(HikariPoolMXBean::getIdleConnections);
    }

    @Override
    public final int getTotalConnections() {
        return getPoolStatus(HikariPoolMXBean::getTotalConnections);
    }

    @Override
    public final int getThreadsAwaitingConnection() {
        return getPoolStatus(HikariPoolMXBean::getThreadsAwaitingConnection);
    }

    private int getPoolStatus(ToIntFunction<HikariPoolMXBean> method) {
        if (delegate.getHikariPoolMXBean() == null) {
            throw new IllegalStateException("Pool state is invalid for this request");
        }
        return method.applyAsInt(delegate.getHikariPoolMXBean());
    }

    @Override
    public final void close() {
        delegate.close();
    }

    @Override
    public String toString() {
        return getUsername() + "@" + getJdbcUrl();
    }
}
