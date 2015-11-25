/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;

/**
 * To store data in session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface SessionStore {

    /**
     * Get or create the session identifier and initialize the session with it if necessary.
     *
     * @param context the web context
     * @return the session identifier
     */
    String getOrCreateSessionId(WebContext context);

    /**
     * Get the object from its key in store.
     *
     * @param context the web context
     * @param key the key of the object
     * @return the object in store
     */
    Object get(WebContext context, String key);

    /**
     * Save an object in the store by its key.
     *
     * @param context the web context
     * @param key the key of the object
     * @param value the value to save in store
     */
    void set(WebContext context, String key, Object value);
}
