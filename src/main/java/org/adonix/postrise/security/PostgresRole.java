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

public final class PostgresRole {

    private String roleName;
    private boolean isSuperUser;
    private boolean isLoginRole;
    private boolean isInheritRole;
    private boolean isCreateRole;
    private boolean isCreateDbRole;
    private boolean isReplicationRole;
    private int connectionLimit;

    PostgresRole() {
    }

    PostgresRole setRoleName(final String roleName) {
        this.roleName = roleName;
        return this;
    }

    PostgresRole setSuperUser(final boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
        return this;
    }

    PostgresRole setLoginRole(final boolean isLoginUser) {
        this.isLoginRole = isLoginUser;
        return this;
    }

    PostgresRole setInherit(final boolean isInherit) {
        this.isInheritRole = isInherit;
        return this;
    }

    PostgresRole setCreateRole(final boolean isCreateRole) {
        this.isCreateRole = isCreateRole;
        return this;
    }

    PostgresRole setCreateDbRole(final boolean isCreateDbRole) {
        this.isCreateDbRole = isCreateDbRole;
        return this;
    }

    PostgresRole setReplicationRole(final boolean isReplicationRole) {
        this.isReplicationRole = isReplicationRole;
        return this;
    }

    PostgresRole setConnectionLimit(final int connectionLimit) {
        this.connectionLimit = connectionLimit;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public boolean isLoginRole() {
        return isLoginRole;
    }

    public boolean isInheritRole() {
        return isInheritRole;
    }

    public boolean isCreateRole() {
        return isCreateRole;
    }

    public boolean isCreateDbRole() {
        return isCreateDbRole;
    }

    public boolean isReplicationRole() {
        return isReplicationRole;
    }

    public int getConnectionLimit() {
        return connectionLimit;
    }
}
