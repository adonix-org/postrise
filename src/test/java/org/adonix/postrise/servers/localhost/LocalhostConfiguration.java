package org.adonix.postrise.servers.localhost;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.ConfigurationListener;
import org.adonix.postrise.JsonConfigurationFile;

public class LocalhostConfiguration extends JsonConfigurationFile implements ConfigurationListener {

    private static final Path JSON_CONFIG_FILE = Paths.get("config", "postrise.json");

    public LocalhostConfiguration() {
        super();
    }

    @Override
    protected Path getJsonFile() {
        return JSON_CONFIG_FILE;
    }

    @Override
    public void onConfigure(ConnectionSettings settings) {
        configuration.getString("server");
    }
}
