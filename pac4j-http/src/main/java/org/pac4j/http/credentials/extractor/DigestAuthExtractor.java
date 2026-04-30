package org.pac4j.http.credentials.extractor;

import lombok.ToString;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.http.credentials.DigestCredentials;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        val realm = valueMap.get("realm");
        val nonce = valueMap.get("nonce");
        val uri = valueMap.get("uri");
        val response = valueMap.get("response");
        val nc = valueMap.get("nc");
        val qop = valueMap.get("qop");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(realm) || StringUtils.isBlank(nonce)
            || StringUtils.isBlank(uri) || StringUtils.isBlank(response)) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        val cnonce = valueMap.get("cnonce");
        if (StringUtils.isNotBlank(qop) && !"auth".equals(qop)) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        if (StringUtils.isNotBlank(nc) && !nc.matches("^[0-9a-fA-F]{8}$")) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        val method = ctx.webContext().getRequestMethod();

        return Optional.of(new DigestCredentials(response, method, username, realm, nonce, uri, cnonce, nc, qop));
    }
    private Map<String, String> parseTokenValue(final String token) {
        val directives = splitTokenDirectives(token);
        Map<String, String> map = new HashMap<>();
        for (val directive : directives) {
            val keyValueIndex = directive.indexOf('=');
            if (keyValueIndex <= 0) {
                throw new CredentialsException("Bad format of the digest auth header");
            }
            val key = directive.substring(0, keyValueIndex).trim();
            val rawValue = directive.substring(keyValueIndex + 1).trim();
            if (StringUtils.isBlank(key) || StringUtils.isBlank(rawValue)) {
                throw new CredentialsException("Bad format of the digest auth header");
            }
            map.put(key, parseDirectiveValue(rawValue));
        }
        return map;
    }

    private List<String> splitTokenDirectives(final String token) {
        List<String> directives = new ArrayList<>();
        val current = new StringBuilder();
        var inQuotes = false;
        var escaping = false;
        for (var i = 0; i < token.length(); i++) {
            val character = token.charAt(i);
            if (escaping) {
                current.append(character);
                escaping = false;
                continue;
            }
            if (character == '\\' && inQuotes) {
                current.append(character);
                escaping = true;
                continue;
            }
            if (character == '\"') {
                current.append(character);
                inQuotes = !inQuotes;
                continue;
            }
            if (character == ',' && !inQuotes) {
                val directive = current.toString().trim();
                if (!directive.isEmpty()) {
                    directives.add(directive);
                }
                current.setLength(0);
                continue;
            }
            current.append(character);
        }
        if (inQuotes) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        val directive = current.toString().trim();
        if (!directive.isEmpty()) {
            directives.add(directive);
        }
        return directives;
    }

    private String parseDirectiveValue(final String rawValue) {
        if (rawValue.startsWith("\"")) {
            if (!rawValue.endsWith("\"") || rawValue.length() < 2) {
                throw new CredentialsException("Bad format of the digest auth header");
            }
            val quotedValue = rawValue.substring(1, rawValue.length() - 1);
            return quotedValue
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .trim();
        }
        return rawValue.replace("\\\"", Pac4jConstants.EMPTY_STRING).trim();
    }
}
