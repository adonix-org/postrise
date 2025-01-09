package org.adonix.postrise.servers.localhost;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.DataSourceListener;
import org.adonix.postrise.JsonConfigurationFile;

public class LocalhostJson extends JsonConfigurationFile implements DataSourceListener {

    private static final Path JSON_CONFIG_FILE = Paths.get("config", "postrise.json");

    @Override
    protected Path getJsonFile() {
        return JSON_CONFIG_FILE;
    }

    @Override
    public void onConfigure(ConnectionSettings settings) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onConfigure'");
    }
}
