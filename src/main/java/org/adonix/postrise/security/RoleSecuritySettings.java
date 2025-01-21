package org.adonix.postrise.security;

public interface RoleSecuritySettings {

    RoleSecurityEvent getSecurity();

    void setSecurity(RoleSecurityEvent listener);
}
