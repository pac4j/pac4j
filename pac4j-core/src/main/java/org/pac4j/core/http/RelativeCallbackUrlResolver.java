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
package org.pac4j.core.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;

/**
 * The new callback url is computed from a relative callback url and the current server configuration.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class RelativeCallbackUrlResolver implements CallbackUrlResolver {

    @Override
    public String compute(final String callbackUrl, WebContext context) {
        if (context != null && callbackUrl != null && !callbackUrl.startsWith("http://") && !callbackUrl.startsWith("https://")) {
            final StringBuilder sb = new StringBuilder();

            sb.append(context.getScheme()).append("://").append(context.getServerName());

            if(("http".contentEquals(context.getScheme()) && context.getServerPort() != HttpConstants.DEFAULT_PORT) || 
              ("https".contentEquals(context.getScheme()) && context.getServerPort() != HttpConstants.DEFAULT_HTTPS_PORT)) {
                sb.append(":").append(context.getServerPort());
            }

            sb.append(callbackUrl.startsWith("/") ? callbackUrl : "/" + callbackUrl);

            return sb.toString();
        }

        return callbackUrl;
    }
}
