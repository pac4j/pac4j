package org.pac4j.saml.util;

import lombok.EqualsAndHashCode;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is {@link SAML2UrlResource}.
 *
 * @author Misagh Moayyed
 */
@EqualsAndHashCode(callSuper = true)
public class SAML2UrlResource extends UrlResource {
    private final SAML2Configuration saml2Configuration;

    public SAML2UrlResource(final URL url, final SAML2Configuration saml2Configuration) {
        super(url);
        this.saml2Configuration = saml2Configuration;
    }

    @Override
    protected void customizeConnection(final HttpURLConnection connection) throws IOException {
        super.customizeConnection(connection);
        if (connection instanceof HttpsURLConnection httpsURLConnection) {
            if (saml2Configuration.getSslSocketFactory() != null) {
                httpsURLConnection.setSSLSocketFactory(saml2Configuration.getSslSocketFactory());
            }
            if (saml2Configuration.getHostnameVerifier() != null) {
                httpsURLConnection.setHostnameVerifier(saml2Configuration.getHostnameVerifier());
            }
        }
    }
}
