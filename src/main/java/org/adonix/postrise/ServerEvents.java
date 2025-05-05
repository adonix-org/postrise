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
 * {@link Server} implementations can override these events as required.
 */
interface ServerEvents {

    /**
     * Event will be dispatched during {@link Server} construction.
     */
    default void onInit() {
    }

    /**
     * Event will be dispatched before the {@link Server} closes.
     */
    default void beforeClose() {
    }

    /**
     * Event will be dispatched after the {@link Server} closes.
     */
    default void afterClose() {
    }

    /**
     * Event will be dispatched when an exception occurs that should not be thrown.
     * 
     * @param e - the exception captured.
     */
    default void onException(final Exception e) {
    }
}
