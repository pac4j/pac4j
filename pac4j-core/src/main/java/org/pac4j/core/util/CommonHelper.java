/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gathers all the utilities methods.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CommonHelper {

    private static final Logger logger = LoggerFactory.getLogger(CommonHelper.class);

    private static final String RESOURCE_PREFIX = "resource:";

    private static final String FILE_PREFIX = "file:";

    /**
     * Return if the String is not blank.
     * 
     * @param s
     * @return if the String is not blank
     */
    public static boolean isNotBlank(final String s) {
        if (s == null) {
            return false;
        }
        return s.trim().length() > 0;
    }

    /**
     * Return if the String is blank.
     * 
     * @param s
     * @return if the String is blank
     */
    public static boolean isBlank(final String s) {
        return !isNotBlank(s);
    }

    /**
     * Compare two String to see if they are equals (both null is ok).
     * 
     * @param s1
     * @param s2
     * @return if two String are equals
     */
    public static boolean areEquals(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    /**
     * Compare two String to see if they are not equals.
     * 
     * @param s1
     * @param s2
     * @return if two String are not equals
     */
    public static boolean areNotEquals(final String s1, final String s2) {
        return !areEquals(s1, s2);
    }

    /**
     * Verify that a String is not blank otherwise throw an {@link TechnicalException}.
     * 
     * @param name
     * @param value
     */
    public static void assertNotBlank(final String name, final String value) {
        if (isBlank(value)) {
            throw new TechnicalException(name + " cannot be blank");
        }
    }

    /**
     * Verify that an Object is not <code>null</code> otherwise throw an {@link TechnicalException}.
     * 
     * @param name
     * @param obj
     */
    public static void assertNotNull(final String name, final Object obj) {
        if (obj == null) {
            throw new TechnicalException(name + " cannot be null");
        }
    }

    /**
     * Add a new parameter to an url.
     * 
     * @param url
     * @param name
     * @param value
     * @return the new url with the parameter appended
     */
    public static String addParameter(final String url, final String name, final String value) {
        if (url != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (name != null) {
                if (url.indexOf("?") >= 0) {
                    sb.append("&");
                } else {
                    sb.append("?");
                }
                sb.append(name);
                sb.append("=");
                if (value != null) {
                    sb.append(encodeText(value));
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Encode a text using UTF-8.
     * 
     * @param url
     * @return the encoded text
     */
    private static String encodeText(final String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            logger.error("Unable to encode text : {} / {}", text, e);
            throw new TechnicalException(e);
        }
    }

    /**
     * Build a normalized "toString" text for an object.
     * 
     * @param clazz
     * @param args
     * @return a normalized "toString" text
     */
    public static String toString(final Class<?> clazz, final Object... args) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(clazz.getSimpleName());
        sb.append("> |");
        boolean b = true;
        for (final Object arg : args) {
            if (b) {
                sb.append(" ");
                sb.append(arg);
                sb.append(":");
            } else {
                sb.append(" ");
                sb.append(arg);
                sb.append(" |");
            }
            b = !b;
        }
        return sb.toString();
    }

    /**
     * Returns an {@link URL} from given name depending on its format:
     * - loads from the classloader if name starts with "resource:"
     * - loads as standard {@link URL} as fallback
     * 
     * @param name
     * @return
     */
    public static URL getURLFromName(String name) {
        try {
            if (name.startsWith(RESOURCE_PREFIX)) {
                String path = name.substring(RESOURCE_PREFIX.length());
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                return CommonHelper.class.getResource(path);
            } else {
                String path = name;
                if (!name.startsWith(FILE_PREFIX)) {
                    path = FILE_PREFIX + name;
                }
                return new URL(path);
            }
        } catch (MalformedURLException e) {
            throw new TechnicalException(e);
        }
    }
}
