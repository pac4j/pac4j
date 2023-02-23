package org.pac4j.saml.util;

import lombok.val;
import net.shibboleth.shared.net.URIComparator;
import net.shibboleth.shared.net.URIException;
import org.apache.hc.core5.net.URIBuilder;

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
            val destinationWithoutParams = new URIBuilder(destination).clearParameters().toString();
            val endpointWithoutParams = new URIBuilder(endpoint).clearParameters().toString();
            return destinationWithoutParams.equalsIgnoreCase(endpointWithoutParams);
        } catch (final Exception e) {
            throw new URIException(e);
        }
    }
}
