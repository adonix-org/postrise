package org.adonix.postrise.servers.localhost;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.adonix.postrise.ConnectionSettings;
import org.adonix.postrise.JsonConfigurationProvider;

public class LocalhostJson extends JsonConfigurationProvider {

    private static final Path jsonFile = Paths.get("config", "postrise.json");

    @Override
    public void onConfigure(final ConnectionSettings settings) {

    }

    @Override
    protected Path getJsonFile() {
        return jsonFile;
    }
}
