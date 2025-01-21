package org.adonix.postrise.security;

import org.adonix.postrise.DataSourceListener;

public interface RoleSecuritySettings {

    DataSourceListener getRoleSecurity();

    void setRoleSecurity(DataSourceListener listener);
}
