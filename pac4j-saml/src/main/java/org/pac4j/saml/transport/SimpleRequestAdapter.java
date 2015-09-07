/*
  Copyright 2012 -2014 pac4j organization

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

package org.pac4j.saml.transport;

import org.pac4j.core.context.WebContext;

/**
 * Basic RequestAdapter returning an inputStream from the input content of
 * the {@link WebContext}.
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.5.0
 *
 */
public class SimpleRequestAdapter {
    private final WebContext context;

    public SimpleRequestAdapter(final WebContext request) {
        this.context = request;
    }

    public final WebContext getContext() {
        return context;
    }

    public final String getMethod() {
        return getContext().getRequestMethod();
    }

    public String getParameterValue(final String arg0) {
        return getContext().getRequestParameter(arg0);
    }
}
