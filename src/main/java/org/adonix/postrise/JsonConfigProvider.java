package org.adonix.postrise;

import java.nio.file.Path;

public class JsonConfigProvider implements DataSourceListener {

    public final Path jsonFilePath;

    public JsonConfigProvider(final Path jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    @Override
    public void onConfigure(final ConnectionSettings settings) {
        settings.setJdbcUrl(settings.getJdbcUrl("hostname", 5432));
        settings.setUsername("username");
    }
}
