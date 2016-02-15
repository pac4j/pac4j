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
 * A helper for the web context.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class ContextHelper {

    /**
     * Get a specific cookie by its name.
     *
     * @param cookies provided cookies
     * @param name the name of the cookie
     * @return the cookie
     */
    public static Cookie getCookie(final Collection<Cookie> cookies, final String name) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie != null && CommonHelper.areEquals(name, cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * Get a specific cookie by its name.
     *
     * @param context the current web context
     * @param name the name of the cookie
     * @return the cookie
     */
    public static Cookie getCookie(final WebContext context, final String name) {
        return getCookie(context.getRequestCookies(), name);
    }

    /**
     * Whether it is a POST request.
     *
     * @param context the web context
     * @return whether it is a POST request
     */
    public static boolean isPost(final WebContext context) {
        return HttpConstants.HTTP_METHOD.POST.name().equalsIgnoreCase(context.getRequestMethod());
    }

    /**
     * Whether the request is HTTPS or secure.
     *
     * @param context the current web context
     * @return whether the request is HTTPS or secure
     */
    public static boolean isHttpsOrSecure(final WebContext context) {
        return "HTTPS".equalsIgnoreCase(context.getScheme()) || context.isSecure();
    }

    /**
     * Whether the request is HTTP.
     *
     * @param context the current web context
     * @return whether the request is HTTP
     */
    public static boolean isHttp(final WebContext context) {
        return "HTTP".equalsIgnoreCase(context.getScheme());
    }

    /**
     * Whether the request is HTTPS.
     *
     * @param context the current web context
     * @return whether the request is HTTPS
     */
    public static boolean isHttps(final WebContext context) {
        return "HTTPS".equalsIgnoreCase(context.getScheme());
    }
}
