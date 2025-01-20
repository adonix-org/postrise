package org.adonix.postrise.security;

public interface SecurityProvider {

    SecurityListener getSecurity();

    void setSecurity(SecurityListener listener);
}
