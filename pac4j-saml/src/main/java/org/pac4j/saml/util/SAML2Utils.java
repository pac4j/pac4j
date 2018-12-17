package org.pac4j.saml.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML2 utilities.
 * 
 * @author jkacer
 * @since 1.8.0
 */
public final class SAML2Utils implements HttpConstants {

    /** SLF4J logger. */
    private static final Logger logger = LoggerFactory.getLogger(SAML2Utils.class);

    /**
     * Private constructor, to prevent instantiation of this utility class.
     */
    private SAML2Utils() {
        super();
    }

    public static String generateID() {
        return "_".concat(CommonHelper.randomString(39)).toLowerCase();
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
        if (uri1 == null && uri2 == null) {
            return true;
        }
        if (uri1 == null || uri2 == null) {
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

        if (SCHEME_HTTP.equals(scheme) && port == DEFAULT_HTTP_PORT) {
            port = -1;
        }
        if (SCHEME_HTTPS.equals(scheme) && port == DEFAULT_HTTPS_PORT) {
            port = -1;
        }
        
        final URI result = new URI(scheme, uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(), uri.getFragment());
        return result;
    }
}
