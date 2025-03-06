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

/**
 * A simple guard clause implementation.
 */
public abstract class Guard {

    protected static final String NULL_OBJECT_ERROR = "Illegal NULL Object for ";
    protected static final String NULL_STRING_ERROR = "Illegal NULL String for ";
    protected static final String EMPTY_STRING_ERROR = "Illegal EMPTY String for ";

    /**
     * The {@code private} constructor. Access {@code public} methods statically.
     */
    private Guard() {
    }

    /**
     * Checks if the expected {@link Object} value is {@code NULL}.
     * 
     * @param parameter - the name of the parameter.
     * @param value     - the {@link Object} to be validated.
     * @throws IllegalArgumentException if the value is {@code NULL}.
     */
    public static final void check(final String parameter, final Object value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(NULL_OBJECT_ERROR + parameter);
        }
    }

    /**
     * Checks if the expected {@link String} value is {@code NULL} or
     * {@code EMPTY}.
     * 
     * @param parameter - the name of the parameter.
     * @param value     - the {@link String} to be validated.
     * @throws IllegalArgumentException if the value is {@code NULL} or
     *                                  {@code EMPTY}.
     */
    public static final void check(final String parameter, final String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(NULL_STRING_ERROR + parameter);
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(EMPTY_STRING_ERROR + parameter);
        }
    }
}
