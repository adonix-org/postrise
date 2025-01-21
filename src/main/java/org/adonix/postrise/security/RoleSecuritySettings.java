package org.adonix.postrise.security;

public interface RoleSecuritySettings {

    RoleSecurityListener getSecurity();

    void setSecurity(RoleSecurityListener listener);
}
