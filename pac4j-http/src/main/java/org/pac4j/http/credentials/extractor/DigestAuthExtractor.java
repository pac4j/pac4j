package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.UsernamePasswordCredentials;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * To extract digest auth header.
 *
 * @author Mircea Carasel
 */
public class DigestAuthExtractor implements Extractor<DigestCredentials> {

    private final HeaderExtractor extractor;

    private final String clientName;

    public DigestAuthExtractor(final String clientName) {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DIGEST_HEADER_PREFIX, clientName);
    }

    public DigestAuthExtractor(final String headerName, final String prefixHeader, final String clientName) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader, clientName);
        this.clientName = clientName;
    }

    @Override
    public DigestCredentials extract(WebContext context) {
        final TokenCredentials credentials = this.extractor.extract(context);

        if (credentials == null) {
            return null;
        }

        String value = credentials.getToken();
        String method = context.getRequestMethod();

        return new DigestCredentials(value, method, clientName);
    }
}
