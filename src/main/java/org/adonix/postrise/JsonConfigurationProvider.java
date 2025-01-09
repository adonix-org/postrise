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
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class JsonConfigurationProvider implements DataSourceListener {

    private final JSONObject configuration;

    public JsonConfigurationProvider() {
        try {
            configuration = new JSONObject(new JSONTokener(Files.readString(getJsonFile())));
        } catch (Exception e) {
            throw new JsonConfigurationException(e);
        }
    }

    public final JSONObject getConfiguration() {
        return configuration;
    }

    protected abstract Path getJsonFile();
}
