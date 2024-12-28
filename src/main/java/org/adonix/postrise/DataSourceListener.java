package org.adonix.postrise;

import java.util.EventListener;

interface DataSourceListener extends EventListener {

    void onCreate(ConnectionSettings settings);
}
