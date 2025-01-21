package org.adonix.postrise.security;

public interface RoleSecuritySettings {

    RoleSecurityListener getRoleSecurity();

    void setRoleSecurity(RoleSecurityListener listener);
}
