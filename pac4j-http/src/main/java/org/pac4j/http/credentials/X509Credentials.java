package org.pac4j.http.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.pac4j.core.credentials.AuthenticationCredentials;

import java.security.cert.X509Certificate;

/**
 * X509 credentials.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
@EqualsAndHashCode
@Getter
public class X509Credentials extends AuthenticationCredentials {

    private static final long serialVersionUID = 2733744949087200768L;

    private final X509Certificate certificate;

    public X509Credentials(final X509Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public String toString() {
        return "X509Credentials(certificate.serialNumber=" + (certificate != null ? certificate.getSerialNumber() : null) + ")";
    }
}
