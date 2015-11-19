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

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;

import javax.servlet.http.HttpSession;

/**
 * Store data in the J2E session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class J2ESessionStore implements SessionStore {

    private HttpSession getHttpSession(final WebContext context) {
        final J2EContext j2EContext = (J2EContext) context;
        return j2EContext.getRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(WebContext context) {
        return getHttpSession(context).getId();
    }

    @Override
    public Object get(WebContext context, String key) {
        return getHttpSession(context).getAttribute(key);
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        getHttpSession(context).setAttribute(key, value);
    }
}
