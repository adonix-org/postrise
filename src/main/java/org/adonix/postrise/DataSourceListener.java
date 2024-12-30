package org.adonix.postrise;

import java.util.EventListener;

public interface DataSourceListener extends EventListener {

    void onConfigure(ConnectionSettings settings);
}
