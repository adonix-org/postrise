package org.adonix.postrise.servers.localhost;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.JsonConfigurationFile;

public class LocalhostJson extends JsonConfigurationFile {

    private static final Path JSON_CONFIG_FILE = Paths.get("config", "postrise.json");

    @Override
    public void onConfigure(final ConnectionSettings settings) {
    }

    @Override
    protected Path getJsonFile() {
        return JSON_CONFIG_FILE;
    }
}
