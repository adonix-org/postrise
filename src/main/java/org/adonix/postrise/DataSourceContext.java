package org.adonix.postrise;

import org.adonix.postrise.security.RoleSecuritySettings;

public interface DataSourceContext
        extends ConnectionPoolSettings, ConnectionPoolStatus, ConnectionSettingsRuntime, RoleSecuritySettings {
}
