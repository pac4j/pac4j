package org.pac4j.http.credentials.extractor;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
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
@ToString
public class DigestAuthExtractor implements CredentialsExtractor {

    private final HeaderExtractor extractor;

    /**
     * <p>Constructor for DigestAuthExtractor.</p>
     */
    public DigestAuthExtractor() {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DIGEST_HEADER_PREFIX);
    }

    /**
     * <p>Constructor for DigestAuthExtractor.</p>
     *
     * @param headerName a {@link String} object
     * @param prefixHeader a {@link String} object
     */
    public DigestAuthExtractor(final String headerName, final String prefixHeader) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader);
    }

    /**
     * {@inheritDoc}
     *
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
     */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val credentials = this.extractor.extract(ctx);
        if (credentials.isEmpty()) {
            return Optional.empty();
        }

        val token = ((TokenCredentials) credentials.get()).getToken();
        val valueMap = parseTokenValue(token);
        val username = valueMap.get("username");
        val response = valueMap.get("response");

        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(response)) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        val realm = valueMap.get("realm");
        val nonce = valueMap.get("nonce");
        val uri = valueMap.get("uri");
        val cnonce = valueMap.get("cnonce");
        val nc = valueMap.get("nc");
        val qop = valueMap.get("qop");
        val method = ctx.webContext().getRequestMethod();

        return Optional.of(new DigestCredentials(response, method, username, realm, nonce, uri, cnonce, nc, qop));
    }

    private Map<String, String> parseTokenValue(String token) {
        val tokenizer = new StringTokenizer(token, ", ");
        String keyval;
        Map<String, String> map = new HashMap<>();
        while (tokenizer.hasMoreElements()) {
            keyval = tokenizer.nextToken();
            if (keyval.contains("=")) {
                val key = keyval.substring(0, keyval.indexOf("="));
                val value = keyval.substring(keyval.indexOf("=") + 1);
                map.put(key.trim(), value.replaceAll("\"", Pac4jConstants.EMPTY_STRING).trim());
            }
        }
        return map;
    }
}
