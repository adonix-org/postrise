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

package org.adonix.postrise.security;

public abstract class SecurityProviders {

    private SecurityProviders() {
    }

    /**
     * PostgreSQL specific security providers.
     */
    public static final SecurityProvider POSTGRES_DEFAULT_SECURITY = new PostgresDefaultSecurity();
    public static final SecurityProvider POSTGRES_STRICT_SECURITY = new PostgresStrictSecurity();

    /**
     * No-op security provider.
     */
    public static final SecurityProvider DISABLE_SECURITY = new DisableSecurity();
}
