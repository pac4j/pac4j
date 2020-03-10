package org.pac4j.http.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * X509 credentials.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public class X509Credentials extends Credentials {

    private static final long serialVersionUID = 2733744949087200768L;

    private final X509Certificate certificate;

    public X509Credentials(final X509Certificate certificate) {
        this.certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final X509Credentials that = (X509Credentials) o;
        return Objects.equals(certificate, that.certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate);
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(getClass(),
            "certificate.serialNumber", certificate != null ? certificate.getSerialNumber() : null);
    }
}
