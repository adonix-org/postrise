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

package org.adonix.postrise.security;

/**
 * A PostgreSQL {@code ROLE} defined by the read-only {@code pg_roles} view.
 * 
 * @see <a href=
 *      "https://www.postgresql.org/docs/current/view-pg-roles.html">pg_roles</a>
 */
public final class PostgresRole {

    private String roleName;
    private boolean isSuperUser;
    private boolean isLoginRole;
    private boolean isInheritRole;
    private boolean isCreateRole;
    private boolean isCreateDbRole;
    private boolean isReplicationRole;
    private int connectionLimit;

    /**
     * The package-private constructor. Instances are created by
     * {@link PostgresRoleDAO} class.
     */
    PostgresRole() {
    }

    /**
     * @param roleName - rolname from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setRoleName(final String roleName) {
        this.roleName = roleName;
        return this;
    }

    /**
     * @param isSuperUser - rolsuper from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setSuperUser(final boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
        return this;
    }

    /**
     * @param isLoginUser - rolcanlogin from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setLoginRole(final boolean isLoginUser) {
        this.isLoginRole = isLoginUser;
        return this;
    }

    /**
     * @param isInherit - rolinherit from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setInherit(final boolean isInherit) {
        this.isInheritRole = isInherit;
        return this;
    }

    /**
     * @param isCreateRole - rolcreaterole from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setCreateRole(final boolean isCreateRole) {
        this.isCreateRole = isCreateRole;
        return this;
    }

    /**
     * @param isCreateDbRole - rolcreatedb from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setCreateDbRole(final boolean isCreateDbRole) {
        this.isCreateDbRole = isCreateDbRole;
        return this;
    }

    /**
     * @param isReplicationRole - rolreplication from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setReplicationRole(final boolean isReplicationRole) {
        this.isReplicationRole = isReplicationRole;
        return this;
    }

    /**
     * @param connectionLimit - rolconnlimit from the {@code pg_roles} view.
     * @return {@link PostgresRole} - instance for method chaining.
     */
    PostgresRole setConnectionLimit(final int connectionLimit) {
        this.connectionLimit = connectionLimit;
        return this;
    }

    /**
     * @return rolname from the {@code pg_roles} view.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @return rolsuper from the {@code pg_roles} view.
     */
    public boolean isSuperUser() {
        return isSuperUser;
    }

    /**
     * @return rolcanlogin from the {@code pg_roles} view.
     */
    public boolean isLoginRole() {
        return isLoginRole;
    }

    /**
     * @return rolinherit from the {@code pg_roles} view.
     */
    public boolean isInheritRole() {
        return isInheritRole;
    }

    /**
     * @return rolcreaterole from the {@code pg_roles} view.
     */
    public boolean isCreateRole() {
        return isCreateRole;
    }

    /**
     * @return rolcreatedb from the {@code pg_roles} view.
     */
    public boolean isCreateDbRole() {
        return isCreateDbRole;
    }

    /**
     * @return rolreplication from the {@code pg_roles} view.
     */
    public boolean isReplicationRole() {
        return isReplicationRole;
    }

    /**
     * @return rolconnlimit from the {@code pg_roles} view.
     */
    public int getConnectionLimit() {
        return connectionLimit;
    }
}
