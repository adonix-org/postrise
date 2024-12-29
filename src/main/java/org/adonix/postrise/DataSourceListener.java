package org.adonix.postrise;

import java.util.EventListener;

public interface DataSourceListener extends EventListener {

    void onCreate(ConnectionSettings settings);
}
