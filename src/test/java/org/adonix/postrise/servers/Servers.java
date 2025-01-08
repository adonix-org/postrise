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

import org.adonix.postrise.Server;
import org.adonix.postrise.servers.localhost.Localhost;
import org.adonix.postrise.servers.localhost.LocalhostSuper;

public final class Servers {

    private static final Server LOCALHOST = new Localhost();
    private static final Server LOCALHOST_SUPER = new LocalhostSuper();

    public static final Server getLocalhost() {
        return LOCALHOST;
    }

    public static final Server getLocalhostSuper() {
        return LOCALHOST_SUPER;
    }
}
