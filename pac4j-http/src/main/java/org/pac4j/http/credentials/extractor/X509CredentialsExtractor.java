package org.pac4j.http.credentials.extractor;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.http.credentials.X509Credentials;

import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * The X509 credentials extractor. Like the X509AuthenticationFilter in Spring Security.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
@Slf4j
public class X509CredentialsExtractor implements CredentialsExtractor  {

    public static final String CERTIFICATE_REQUEST_ATTRIBUTE = "javax.servlet.request.X509Certificate";

    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val certificates =
            (Optional<X509Certificate[]>) ctx.webContext().getRequestAttribute(CERTIFICATE_REQUEST_ATTRIBUTE);

        if (certificates.isPresent() && certificates.get().length > 0) {
            val certificate = certificates.get()[0];
            LOGGER.debug("X509 certificate: {}", certificate);

            return Optional.of(new X509Credentials(certificate));
        }

        LOGGER.debug("No X509 certificate in request");
        return Optional.empty();
    }
}
