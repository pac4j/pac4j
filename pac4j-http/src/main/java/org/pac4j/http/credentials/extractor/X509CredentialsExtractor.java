package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.http.credentials.X509Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * The X509 credentials extractor. Like the X509AuthenticationFilter in Spring Security.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public class X509CredentialsExtractor implements CredentialsExtractor<X509Credentials>  {

    public static final String CERTIFICATE_REQUEST_ATTRIBUTE = "javax.servlet.request.X509Certificate";

    private static final Logger logger = LoggerFactory.getLogger(X509CredentialsExtractor.class);

    @Override
    public Optional<X509Credentials> extract(WebContext context) {
        final X509Certificate[] certificates = (X509Certificate[]) context.getRequestAttribute(CERTIFICATE_REQUEST_ATTRIBUTE);

        if (certificates != null && certificates.length > 0) {
            final X509Certificate certificate = certificates[0];
            logger.debug("X509 certificate: {}", certificate);

            return Optional.of(new X509Credentials(certificate));
        }

        logger.debug("No X509 certificate in request");
        return Optional.empty();
    }
}
