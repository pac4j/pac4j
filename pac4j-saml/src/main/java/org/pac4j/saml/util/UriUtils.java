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
 package org.pac4j.saml.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * URI utilities.
 * 
 * @author jkacer
 * @since 1.8.0
 */
public class UriUtils {

    /** HTTP scheme. */
    private static final String SCHEME_HTTP = "http";
    /** HTTPS scheme. */
    private static final String SCHEME_HTTPS = "https";
    
    /** Default HTTP port. */
    private static final int DEFAULT_PORT_HTTP = 80;
    /** Default HTTPS port. */
    private static final int DEFAULT_PORT_HTTPS = 443;
    
    /** SLF4J logger. */
    private static final Logger logger = LoggerFactory.getLogger(UriUtils.class);
    
    
    // ------------------------------------------------------------------------------------------------------------------------------------

    
    /**
     * Private constructor, to prevent instantiation of this utility class.
     */
    private UriUtils() {
        super();
    }


    
    /**
     * Compares two URIs for equality, ignoring default port numbers for selected protocols.
     * 
     * By default, {@link URI#equals(Object)} doesn't take into account default port numbers, so http://server:80/resource is a different
     * URI than http://server/resource.
     * 
     * And URLs should not be used for comparison, as written here:
     * http://stackoverflow.com/questions/3771081/proper-way-to-check-for-url-equality
     * 
     * @param uri1
     *            URI 1 to be compared.
     * @param uri2
     *            URI 2 to be compared.
     * 
     * @return True if both URIs are equal.
     */
    public static boolean urisEqualAfterPortNormalization(final URI uri1, final URI uri2) {
        if ((uri1 == null) && (uri2 == null)) {
            return true;
        }
        if (((uri1 == null) && (uri2 != null)) || ((uri1 != null) && (uri2 == null))) {
            return false;
        }
        
        try {
            URI normalizedUri1 = normalizePortNumbersInUri(uri1);
            URI normalizedUri2 = normalizePortNumbersInUri(uri2);
            boolean eq = normalizedUri1.equals(normalizedUri2);
            return eq;
        } catch (URISyntaxException use) {
            logger.error("Cannot compare 2 URIs.", use);
            return false;    
        }
    }

    
    /**
     * Normalizes a URI. If it contains the default port for the used scheme, the method replaces the port with "default".
     * 
     * @param uri
     *            The URI to normalize.
     * 
     * @return A normalized URI.
     * 
     * @throws URISyntaxException
     *             If a URI cannot be created because of wrong syntax.
     */
    private static URI normalizePortNumbersInUri(final URI uri) throws URISyntaxException {
        int port = uri.getPort();
        final String scheme = uri.getScheme();
        
        if (SCHEME_HTTP.equals(scheme) && (port == DEFAULT_PORT_HTTP)) {
            port = -1;
        }
        if (SCHEME_HTTPS.equals(scheme) && (port == DEFAULT_PORT_HTTPS)) {
            port = -1;
        }
        
        final URI result = new URI(scheme, uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(), uri.getFragment());
        return result;
    }
    
}
