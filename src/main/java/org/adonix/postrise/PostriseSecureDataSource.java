package org.adonix.postrise;

import org.adonix.postrise.security.SecurityListener;

abstract class PostriseSecureDataSource extends PostriseDataSource {

    private SecurityListener security = getDefaultSecurity();

    PostriseSecureDataSource(final String database) {
        super(database);
    }

    abstract SecurityListener getDefaultSecurity();

    @Override
    public SecurityListener getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(final SecurityListener security) {
        this.security = security;
    }
}
