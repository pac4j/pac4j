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
package org.pac4j.core.context;

import org.pac4j.core.util.CommonHelper;

import java.util.Collection;

/**
 * Abstract web context with base implementations.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public abstract class AbstractWebContext implements WebContext {

    @Override
    public Cookie getRequestCookie(String name) {
        final Collection<Cookie> cookies = getRequestCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie != null && CommonHelper.areEquals(name, cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
