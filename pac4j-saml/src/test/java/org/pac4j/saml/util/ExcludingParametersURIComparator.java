package org.pac4j.saml.util;

import net.shibboleth.utilities.java.support.net.URIComparator;
import net.shibboleth.utilities.java.support.net.URIException;
import org.apache.http.client.utils.URIBuilder;

/**
 * This is {@link ExcludingParametersURIComparator}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class ExcludingParametersURIComparator implements URIComparator {
    @Override
    public boolean compare(final String destination, final String endpoint) throws URIException {
        try {
            final var destinationWithoutParams = new URIBuilder(destination).clearParameters().toString();
            final var endpointWithoutParams = new URIBuilder(endpoint).clearParameters().toString();
            return destinationWithoutParams.equalsIgnoreCase(endpointWithoutParams);
        } catch (final Exception e) {
            throw new URIException(e);
        }
    }
}
