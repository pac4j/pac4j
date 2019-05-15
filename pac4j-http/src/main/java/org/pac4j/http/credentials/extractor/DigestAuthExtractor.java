package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.DigestCredentials;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * To extract digest auth header.
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DigestAuthExtractor implements CredentialsExtractor<DigestCredentials> {

    private final HeaderExtractor extractor;

    public DigestAuthExtractor() {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DIGEST_HEADER_PREFIX);
    }

    public DigestAuthExtractor(final String headerName, final String prefixHeader) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader);
    }

    /**
     * Extracts digest Authorization header components.
     * As per RFC 2617 :
     * username is the user's name in the specified realm
     * qop is quality of protection
     * uri is the request uri
     * response is the client response
     * nonce is a server-specified data string which should be uniquely generated
     *   each time a 401 response is made
     * cnonce is the client nonce
     * nc is the nonce count
     * If in the Authorization header it is not specified a username and response, we throw CredentialsException because
     * the client uses an username and a password to authenticate. response is just a MD5 encoded value
     * based on user provided password and RFC 2617 digest authentication encoding rules
     * @param context the current web context
     * @return the Digest credentials
     */
    @Override
    public Optional<DigestCredentials> extract(WebContext context) {
        final Optional<TokenCredentials> credentials = this.extractor.extract(context);
        if (!credentials.isPresent()) {
            return Optional.empty();
        }

        String token = credentials.get().getToken();
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

        return Optional.of(new DigestCredentials(response, method, username, realm, nonce, uri, cnonce, nc, qop));
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

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "extractor", extractor);
    }
}
