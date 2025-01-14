package org.adonix.postrise.security;

final class PostgresRole {

    private String roleName;
    private Boolean isSuperUser;
    private Boolean isLoginRole;
    private Boolean isInheritRole;
    private Boolean isCreateRole;
    private Boolean isCreateDbRole;
    private Boolean isReplicationRole;

    PostgresRole setRoleName(final String roleName) {
        this.roleName = roleName;
        return this;
    }

    PostgresRole setSuperUser(final Boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
        return this;
    }

    PostgresRole setLoginRole(final Boolean isLoginUser) {
        this.isLoginRole = isLoginUser;
        return this;
    }

    PostgresRole setInherit(final Boolean isInherit) {
        this.isInheritRole = isInherit;
        return this;
    }

    PostgresRole setCreateRole(final Boolean isCreateRole) {
        this.isCreateRole = isCreateRole;
        return this;
    }

    PostgresRole setCreateDbRole(final Boolean isCreateDbRole) {
        this.isCreateDbRole = isCreateDbRole;
        return this;
    }

    PostgresRole setReplicationRole(final Boolean isReplicationRole) {
        this.isReplicationRole = isReplicationRole;
        return this;
    }

    String getRoleName() {
        return roleName;
    }

    Boolean isSuperUser() {
        return isSuperUser;
    }

    Boolean isLoginRole() {
        return isLoginRole;
    }

    Boolean isInheritRole() {
        return isInheritRole;
    }

    Boolean isCreateRole() {
        return isCreateRole;
    }

    Boolean isCreateDbRole() {
        return isCreateDbRole;
    }

    Boolean isReplicationRole() {
        return isReplicationRole;
    }
}
