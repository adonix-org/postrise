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

package org.adonix.postrise.servers;

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;

public class EdgeCaseServer extends PostgresServer {

    @Override
    public void beforeCreate(final DataSourceSettings settings) {
        super.beforeCreate(settings);
        settings.setUsername("postrise");
    }

    @Override
    protected void beforeClose() {
        super.beforeClose();
        this.addListener(new PostriseDatabase());
    }

    @Override
    protected void afterClose() {
        super.afterClose();
        this.addListener(new PostriseDatabase());
    }

    @Override
    protected void onException(Exception e) {
        super.onException(e);
        throw new RuntimeException("Do not throw Exception from here.");
    }
}
