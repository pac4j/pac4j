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
package org.pac4j.http.authorization;

import org.pac4j.core.context.WebContext;

/**
 * CSRF token generator.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface CsrfTokenGenerator {

    /**
     * Get the CSRF token from the session or create it if it doesn't exist.
     *
     * @param context the current web context
     * @return the CSRF token
     */
    String get(WebContext context);
}
