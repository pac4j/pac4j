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

        String token = credentials.getToken();
        Map<String, String> valueMap = parseTokenValue(token);
        String username = valueMap.get("username");
        String response = valueMap.get("response");

        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(response)) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        String realm = valueMap.get("realm");
        String nonce = valueMap.get("nonce");
        String uri = valueMap.get("uri");
        String cnonce = valueMap.get("cnonce");
        String nc = valueMap.get("nc");
        String qop = valueMap.get("qop");
        String method = context.getRequestMethod();

        return new DigestCredentials(response, method, clientName, username, realm, nonce, uri, cnonce, nc, qop);
    }

    private Map<String, String> parseTokenValue(String token) {
        StringTokenizer tokenizer = new StringTokenizer(token, ", ");
        String keyval;
        Map map = new HashMap<String, String>();
        while (tokenizer.hasMoreElements()) {
            keyval = tokenizer.nextToken();
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                map.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }
        return map;
    }
}
