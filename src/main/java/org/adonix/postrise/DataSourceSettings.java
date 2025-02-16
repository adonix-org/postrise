package org.adonix.postrise;

import org.adonix.postrise.security.RoleSecuritySettings;

public interface DataSourceSettings extends ConnectionPoolSettings, ConnectionSettingsWrite, RoleSecuritySettings {
}
