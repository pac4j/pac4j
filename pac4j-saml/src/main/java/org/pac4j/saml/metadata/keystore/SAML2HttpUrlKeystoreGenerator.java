package org.pac4j.saml.metadata.keystore;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * This is {@link SAML2HttpUrlKeystoreGenerator}.
 *
 * @author Misagh Moayyed
 */
public class SAML2HttpUrlKeystoreGenerator extends BaseSAML2KeystoreGenerator {

    public SAML2HttpUrlKeystoreGenerator(final SAML2Configuration configuration) {
        super(configuration);
    }

    @Override
    public InputStream retrieve() throws Exception {
        final String url = saml2Configuration.getKeystoreResource().getURL().toExternalForm();
        logger.debug("Loading keystore from {}", url);
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", ContentType.TEXT_PLAIN.getMimeType());
        httpGet.addHeader("Content-Type", ContentType.TEXT_PLAIN.getMimeType());
        HttpResponse response = null;
        try {
            response = saml2Configuration.getHttpClient().execute(httpGet);
            if (response != null) {
                final int code = response.getStatusLine().getStatusCode();
                if (code == HttpStatus.SC_OK) {
                    logger.info("Successfully submitted/created keystore to {}", url);
                    final String results = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    return new ByteArrayInputStream(Base64.getDecoder().decode(results));
                }
            }
            throw new SAMLException("Unable to retrieve keystore from " + url);
        } finally {
            if (response != null && response instanceof CloseableHttpResponse) {
                ((CloseableHttpResponse) response).close();
            }
        }
    }

    @Override
    protected void store(final KeyStore ks, final X509Certificate certificate,
                         final PrivateKey privateKey) throws Exception {
        HttpResponse response = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final char[] password = saml2Configuration.getKeystorePassword().toCharArray();
            ks.store(out, password);
            out.flush();
            final String content = Base64.getEncoder().encodeToString(out.toByteArray());

            if (logger.isTraceEnabled()) {
                logger.trace("Encoded keystore as base-64: {}", content);
            }

            final String url = saml2Configuration.getKeystoreResource().getURL().toExternalForm();
            final HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", ContentType.TEXT_PLAIN.getMimeType());
            httpPost.addHeader("Content-Type", ContentType.TEXT_PLAIN.getMimeType());
            httpPost.setEntity(new StringEntity(content, ContentType.TEXT_PLAIN));
            logger.debug("Submitting keystore to {}", url);

            response = saml2Configuration.getHttpClient().execute(httpPost);
            if (response != null) {
                final int code = response.getStatusLine().getStatusCode();
                if (code == HttpStatus.SC_NOT_IMPLEMENTED) {
                    logger.info("Storing keystore is not supported/implemented by {}", url);
                } else if (code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
                    logger.info("Successfully submitted/created keystore to {}", url);
                } else if (code == HttpStatus.SC_NOT_MODIFIED) {
                    logger.info("Keystore was not modified/updated", url);
                } else {
                    logger.error("Unable to store keystore successfully via {}", url);
                }
            }
        } finally {
            if (response != null && response instanceof CloseableHttpResponse) {
                ((CloseableHttpResponse) response).close();
            }
        }
    }
}
