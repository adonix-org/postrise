/*
 * Copyright (C) 2025 Ty Busby
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adonix.postrise;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class JsonConfigurationFile {

    private static final Logger LOGGER = LogManager.getLogger();

    protected abstract Path getJsonFile();

    protected final JSONObject configuration;

    protected JsonConfigurationFile() {
        try {
            configuration = new JSONObject(new JSONTokener(Files.readString(getJsonFile().toAbsolutePath())));
        } catch (final Exception e) {
            LOGGER.error(getJsonFile().toAbsolutePath(), e);
            throw new JsonConfigurationException(e);
        }
    }

    public final JSONObject getConfiguration() {
        return configuration;
    }
}
