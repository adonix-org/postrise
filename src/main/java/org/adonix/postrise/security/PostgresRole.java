package org.adonix.postrise.security;

final class PostgresRole {

    private String roleName;
    private boolean isSuperUser;
    private boolean isLoginRole;
    private boolean isInheritRole;
    private boolean isCreateRole;
    private boolean isCreateDbRole;
    private boolean isReplicationRole;

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

    String getRoleName() {
        return roleName;
    }

    boolean isSuperUser() {
        return isSuperUser;
    }

    boolean isLoginRole() {
        return isLoginRole;
    }

    boolean isInheritRole() {
        return isInheritRole;
    }

    boolean isCreateRole() {
        return isCreateRole;
    }

    boolean isCreateDbRole() {
        return isCreateDbRole;
    }

    boolean isReplicationRole() {
        return isReplicationRole;
    }
}
