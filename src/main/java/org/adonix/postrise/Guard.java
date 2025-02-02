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

public abstract class Guard {

    protected static final String NULL_OBJECT_ERROR = "Unexpected null Object for ";
    protected static final String NULL_STRING_ERROR = "Unexpected null String for ";
    protected static final String BLANK_STRING_ERROR = "Unexpected empty String for ";

    private Guard() {
    }

    public static final void check(final String parameterName, final Object parameter) throws IllegalArgumentException {
        if (parameter == null) {
            throw new IllegalArgumentException(NULL_OBJECT_ERROR + parameterName);
        }
    }

    public static final void check(final String parameterName, final String parameter) throws IllegalArgumentException {
        if (parameter == null) {
            throw new IllegalArgumentException(NULL_STRING_ERROR + parameterName);
        }
        if (parameter.isBlank()) {
            throw new IllegalArgumentException(BLANK_STRING_ERROR + parameterName);
        }
    }

    public static final void check(final Server server, final boolean isClosed) {
        if (isClosed) {
            throw new IllegalStateException("Server " + server.toString() + " is closed");
        }
    }

    public static final void check(final Server server, final boolean isClosing, final boolean isClosed) {
        if (isClosing) {
            throw new IllegalStateException("Server " + server.toString() + " is closing");
        }
        check(server, isClosed);
    }
}
