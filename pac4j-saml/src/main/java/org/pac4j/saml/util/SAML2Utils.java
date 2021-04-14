package org.pac4j.saml.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
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
    private static final Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");

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
            final var normalizedUri1 = normalizePortNumbersInUri(uri1);
            final var normalizedUri2 = normalizePortNumbersInUri(uri2);
            final var eq = normalizedUri1.equals(normalizedUri2);
            return eq;
        } catch (final URISyntaxException use) {
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
        var port = uri.getPort();
        final var scheme = uri.getScheme();

        if (SCHEME_HTTP.equals(scheme) && port == DEFAULT_HTTP_PORT) {
            port = -1;
        }
        if (SCHEME_HTTPS.equals(scheme) && port == DEFAULT_HTTPS_PORT) {
            port = -1;
        }

        final var result = new URI(scheme, uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(), uri.getFragment());
        return result;
    }

    public static ChainingMetadataResolver buildChainingMetadataResolver(final SAML2MetadataResolver idpMetadataProvider,
                                                                         final SAML2MetadataResolver spMetadataProvider) {
        final var metadataManager = new ChainingMetadataResolver();
        metadataManager.setId(ChainingMetadataResolver.class.getCanonicalName());
        try {
            final List<MetadataResolver> list = new ArrayList<>();
            list.add(idpMetadataProvider.resolve());
            list.add(spMetadataProvider.resolve());
            metadataManager.setResolvers(list);
            metadataManager.initialize();
        } catch (final ResolverException e) {
            throw new TechnicalException("Error adding idp or sp metadatas to manager", e);
        } catch (final ComponentInitializationException e) {
            throw new TechnicalException("Error initializing manager", e);
        }
        return metadataManager;
    }

    public static void logProtocolMessage(final XMLObject object) {
        if (protocolMessageLog.isDebugEnabled()) {
            try {
                final var requestXml = SerializeSupport.nodeToString(XMLObjectSupport.marshall(object));
                protocolMessageLog.debug(requestXml);
            } catch (final MarshallingException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
