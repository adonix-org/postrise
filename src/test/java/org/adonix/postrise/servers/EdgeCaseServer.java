package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;

public class EdgeCaseServer extends PostgresServer {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
    }

    @Override
    protected void beforeClose() {
        super.beforeClose();
        this.addListener(new PostriseDatabase());
    }

    @Override
    protected void afterClose() {
        super.afterClose();
        this.addListener(new PostriseDatabase());
    }

    @Override
    protected void onException(Exception e) {
        super.onException(e);
        throw new RuntimeException("Do not throw Exception from here.");
    }
}
