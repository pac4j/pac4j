package org.pac4j.http.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.pac4j.core.credentials.Credentials;

import java.io.Serial;
import java.security.cert.X509Certificate;

/**
 * X509 credentials.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
@EqualsAndHashCode
@Getter
public class X509Credentials extends Credentials {

    @Serial
    private static final long serialVersionUID = 2733744949087200768L;

    private final X509Certificate certificate;

    /**
     * <p>Constructor for X509Credentials.</p>
     *
     * @param certificate a {@link X509Certificate} object
     */
    public X509Credentials(final X509Certificate certificate) {
        this.certificate = certificate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "X509Credentials(certificate.serialNumber=" + (certificate != null ? certificate.getSerialNumber() : null) + ")";
    }
}
