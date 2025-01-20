package org.adonix.postrise.security;

public interface SecuritySettings {

    SecurityProvider getSecurity();

    void setSecurity(SecurityProvider listener);
}
