package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.X509Credentials;
import org.pac4j.http.credentials.extractor.X509CredentialsExtractor;
import org.pac4j.http.profile.X509Profile;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Tests {@link X509Client}.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public final class X509ClientTests implements TestsConstants {

    private static final String CERTIFICATE = "MIIDdTCCAl2gAwIBAgIEbYmrcjANBgkqhkiG9w0BAQsFADBrMRAwDgYDVQQGEwdV" +
        "bmtub3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYD" +
        "VQQKEwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMQ8wDQYDVQQDEwZqZXJvbWUw" +
        "HhcNMTgxMDEwMDY1MzQ5WhcNMjMxMDA5MDY1MzQ5WjBrMRAwDgYDVQQGEwdVbmtu" +
        "b3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYDVQQK" +
        "EwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMQ8wDQYDVQQDEwZqZXJvbWUwggEi" +
        "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCAHgcLbb3zeyQOnphHZtQb7kbo" +
        "u7RsB86yfkzzQFghhIvQE0WX4NErpG7iLppHA0vVRteAmTOiRrGghDDFczVgjbq0" +
        "PRNkhdTeZRiAnYv6WPZV6lm66wo95VQBe7bolU9paL88BgnZKrbgusRnYfqLirQ7" +
        "CuSDkcPAxAx+dLsUe5KzSsj2vBuCbQRb9vE89EYjExFNbR7av5I20r+CsQZaFzwM" +
        "cHe1NDVgQ/+LFdAos/IYzdoW2RS/TvSB447rPoQGrbConA5J9fCPYo3IxSUGcNsm" +
        "F120tcwSei8W06GRyfCVt5LTbxCTBBYvSBQztovH8tUsjt7P9PjGVYvlYzeVAgMB" +
        "AAGjITAfMB0GA1UdDgQWBBTS2C+1BWx6RdhcuRFT+4/sE4wOZzANBgkqhkiG9w0B" +
        "AQsFAAOCAQEAdnfOoLMV14DyI+3jY0LvKJFG4YjSIrAAjJTUTnbjdF3RleYdRVCS" +
        "Zu8PUOieTQkl7jdYGdPauhZPE4qjkFph7mjsRwj8YkFpgyNLAggGcxb0rst6yv28" +
        "6ldxN7TJH1hdNgSG1BOOCRzgicnC/aSMkQTvC1EWWqHUnOis5oPrCtHg8lPLGb3y" +
        "m45dZl91hdvnrs80IdUanmf287CnRqmG0rq6nmYh73msb9l5t8RbDpqkYaliiEQY" +
        "RLrT3lejJyf1GVhZvxOcXPglcXdQyX1vGxf15mRW91LbyghsGUF3REAmE6K1hWCe" +
        "YT/h4KtrPV/aOyx+fVMum0AuskOTaKF+QQ==";

    private X509Client client = new X509Client();

    @Test
    public void testOk() throws CertificateException {
        final var context = MockWebContext.create();
        final var certificateData = Base64.getDecoder().decode(CERTIFICATE);
        final var cert = (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(certificateData));
        final var certs = new X509Certificate[1];
        certs[0] = cert;
        context.setRequestAttribute(X509CredentialsExtractor.CERTIFICATE_REQUEST_ATTRIBUTE, certs);
        final var credentials = (X509Credentials) client.getCredentials(context, new MockSessionStore()).get();
        final var profile = (X509Profile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals("jerome", profile.getId());
    }
}
