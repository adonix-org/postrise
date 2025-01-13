package org.adonix.postrise.security;

public class PostgresRole {

    private String roleName;
    private Boolean isSuperUser;
    private Boolean isLoginUser;
    private Boolean isInherit;
    private Boolean isCreateRole;
    private Boolean isCreateDbRole;
    private Boolean isReplicationRole;

    protected PostgresRole() {
    }

    protected PostgresRole setRoleName(final String roleName) {
        this.roleName = roleName;
        return this;
    }

    protected PostgresRole setSuperUser(final Boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
        return this;
    }

    protected PostgresRole setLoginUser(final Boolean isLoginUser) {
        this.isLoginUser = isLoginUser;
        return this;
    }

    protected PostgresRole setInherit(final Boolean isInherit) {
        this.isInherit = isInherit;
        return this;
    }

    protected PostgresRole setCreateRole(final Boolean isCreateRole) {
        this.isCreateRole = isCreateRole;
        return this;
    }

    protected PostgresRole setCreateDbRole(final Boolean isCreateDbRole) {
        this.isCreateDbRole = isCreateDbRole;
        return this;
    }

    protected PostgresRole setReplicationRole(final Boolean isReplicationRole) {
        this.isReplicationRole = isReplicationRole;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public Boolean isSuperUser() {
        return isSuperUser;
    }

    public Boolean isLoginRole() {
        return isLoginUser;
    }

    public Boolean isInheritRole() {
        return isInherit;
    }

    public Boolean isCreateRole() {
        return isCreateRole;
    }

    public Boolean isCreateDbRole() {
        return isCreateDbRole;
    }

    public Boolean isReplicationRole() {
        return isReplicationRole;
    }
}
