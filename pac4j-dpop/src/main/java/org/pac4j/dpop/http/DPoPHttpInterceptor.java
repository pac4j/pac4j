package org.pac4j.dpop.http;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.DefaultDPoPProofFactory;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.interceptor.HttpInterceptor;
import org.pac4j.core.http.oauth.OAuthHttpRequest;
import org.pac4j.core.http.oauth.OAuthHttpResponse;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.dpop.util.DPoPProtocolMessages;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

import static org.pac4j.dpop.util.DPoPConstants.*;

/**
 * Adds a DPoP proof (RFC 9449) to the requests sent to the OAuth server by the OAuth clients (an
 * {@link OAuthHttpRequest}) and to the OpenID provider by the OIDC clients (a Nimbus {@link HTTPRequest}).
 *
 * The proof is signed by a key pair saved in the web session, generated on first use: the tokens are bound to it,
 * so the same one proves their possession on the following requests (UserInfo, refresh). A web session is required.
 *
 * When a server requires a nonce (RFC 9449, sections 8 and 9), it is saved in the web session as well, per server,
 * and the refused request is sent again with a proof carrying it.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class DPoPHttpInterceptor implements HttpInterceptor {

    /** The algorithm signing the proofs, of the EC or RSA family: it decides the type of the generated key pair. */
    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.ES256;

    /** {@inheritDoc} */
    @Override
    public void configureRequest(final Object request, final CallContext context) {
        if (request instanceof OAuthHttpRequest oauthRequest) {
            configureOAuthRequest(oauthRequest, context);
        } else if (request instanceof HTTPRequest nimbusRequest) {
            configureNimbusRequest(nimbusRequest, context);
        } else {
            throw new TechnicalException("Unsupported request type: " + (request == null ? null : request.getClass()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleResponse(final Object request, final Object response, final CallContext context) {
        if (request instanceof OAuthHttpRequest oauthRequest && response instanceof OAuthHttpResponse oauthResponse) {
            val headers = oauthResponse.headers();
            return retrieveNonce(oauthRequest.url(), headers.get(DPOP_NONCE_HEADER), headers.get(HttpConstants.AUTHENTICATE_HEADER),
                oauthResponse.body(), context);
        } else if (request instanceof HTTPRequest nimbusRequest && response instanceof HTTPResponse nimbusResponse) {
            val nonce = nimbusResponse.getDPoPNonce();
            return retrieveNonce(nimbusRequest.getURI().toString(), nonce == null ? null : nonce.getValue(),
                nimbusResponse.getWWWAuthenticate(), nimbusResponse.getBody(), context);
        } else {
            throw new TechnicalException("Unsupported request and response types: "
                + (request == null ? null : request.getClass()) + ", " + (response == null ? null : response.getClass()));
        }
    }

    /**
     * Save the nonce provided by the server, if any, for the next proofs sent to it: a server may provide a new one
     * in any response (RFC 9449, section 8.2).
     *
     * @param url the URL of the request
     * @param nonce the value of the DPoP-Nonce header of the response (can be null)
     * @param wwwAuthenticate the value of the WWW-Authenticate header of the response (can be null)
     * @param body the body of the response (can be null)
     * @param context the call context
     * @return whether the server refused the request to require this nonce, in which case it must be sent again
     */
    protected boolean retrieveNonce(final String url, final String nonce, final String wwwAuthenticate, final String body,
                                    final CallContext context) {
        if (CommonHelper.isBlank(nonce)) {
            return false;
        }
        assertSession(context);
        val sessionAttribute = nonceSessionAttribute(url);
        context.sessionStore().set(context.webContext(), sessionAttribute, nonce);
        val required = isNonceRequired(wwwAuthenticate, body);
        DPoPProtocolMessages.receivedNonce(sessionAttribute.substring(NONCE_SESSION_ATTRIBUTE_PREFIX.length()), nonce, required);
        return required;
    }

    /**
     * Whether the response requires a nonce: a resource server says so in the WWW-Authenticate header
     * (RFC 9449, section 9), an authorization server in the error of its JSON body (RFC 9449, section 8).
     *
     * @param wwwAuthenticate the value of the WWW-Authenticate header (can be null)
     * @param body the body (can be null)
     * @return whether a nonce is required
     */
    protected boolean isNonceRequired(final String wwwAuthenticate, final String body) {
        if (wwwAuthenticate != null && wwwAuthenticate.contains(USE_DPOP_NONCE_ERROR)) {
            return true;
        }
        if (body == null || !body.contains(USE_DPOP_NONCE_ERROR)) {
            return false;
        }
        try {
            return USE_DPOP_NONCE_ERROR.equals(JSONObjectUtils.parse(body).get(ERROR));
        } catch (final ParseException e) {
            return false;
        }
    }

    /**
     * Compute the name of the session attribute holding the nonce of a server: the nonces of the authorization
     * server and of a resource server are distinct (RFC 9449, section 9).
     *
     * @param url a URL of the server
     * @return the name of the session attribute
     */
    protected String nonceSessionAttribute(final String url) {
        val uri = URI.create(url);
        return NONCE_SESSION_ATTRIBUTE_PREFIX + uri.getScheme() + "://" + uri.getRawAuthority();
    }

    /**
     * Add the proof to a request of an OAuth client. ScribeJava always sends the access token as a bearer token,
     * while a DPoP-bound access token must use the DPoP scheme (RFC 9449, section 7.1): the scheme is replaced.
     *
     * @param request the request
     * @param context the call context
     */
    protected void configureOAuthRequest(final OAuthHttpRequest request, final CallContext context) {
        val headers = request.headers();
        val accessToken = extractAccessToken(headers.get(HttpConstants.AUTHORIZATION_HEADER));
        headers.put(DPOP_HEADER, buildProof(request.verb(), request.url(), accessToken, context).serialize());
        if (accessToken != null) {
            headers.put(HttpConstants.AUTHORIZATION_HEADER, DPOP_HEADER_PREFIX + accessToken);
        }
    }

    /**
     * Add the proof to a request of an OIDC client. Nimbus already sends the access token with the scheme
     * matching the token type returned by the OpenID provider: the authorization is left untouched.
     *
     * @param request the request
     * @param context the call context
     */
    protected void configureNimbusRequest(final HTTPRequest request, final CallContext context) {
        val accessToken = extractAccessToken(request.getAuthorization());
        request.setDPoP(buildProof(request.getMethod().name(), request.getURI().toString(), accessToken, context));
    }

    /**
     * Extract the access token from an authorization header.
     *
     * @param authorization the header value (can be null)
     * @return the access token, or null when the header carries none (a client authentication for example)
     */
    protected String extractAccessToken(final String authorization) {
        if (authorization != null) {
            for (val prefix : new String[] {HttpConstants.BEARER_HEADER_PREFIX, DPOP_HEADER_PREFIX}) {
                if (authorization.regionMatches(true, 0, prefix, 0, prefix.length())) {
                    return authorization.substring(prefix.length()).trim();
                }
            }
        }
        return null;
    }

    /**
     * Build the proof for a request.
     *
     * @param method the HTTP method
     * @param url the URL
     * @param accessToken the access token sent with the request (can be null)
     * @param context the call context
     * @return the proof
     */
    protected SignedJWT buildProof(final String method, final String url, final String accessToken,
                                   final CallContext context) {
        CommonHelper.assertNotNull("jwsAlgorithm", jwsAlgorithm);
        try {
            val uri = new URI(url);
            // the htu claim is the URI without query and fragment (RFC 9449, section 4.2)
            val htu = new URI(uri.getScheme(), uri.getRawAuthority(), uri.getRawPath(), null, null);
            // the ath claim, hash of the access token, is required when it is presented (RFC 9449, section 4.2)
            val token = accessToken == null ? null : new DPoPAccessToken(accessToken);
            val key = getKey(context);
            // the nonce claim carries the last nonce provided by the server, if any (RFC 9449, section 8)
            val nonce = context.sessionStore().get(context.webContext(), nonceSessionAttribute(url))
                .map(value -> new Nonce((String) value)).orElse(null);
            val proof = new DefaultDPoPProofFactory(key, jwsAlgorithm).createDPoPJWT(method, htu, token, nonce);
            DPoPProtocolMessages.sentProof(method, htu.toString(), proof);
            LOGGER.trace("DPoP proof: header={} claims={}", proof.getHeader(), proof.getPayload());
            return proof;
        } catch (final URISyntaxException | JOSEException e) {
            throw new TechnicalException("Cannot build the DPoP proof", e);
        }
    }

    /**
     * Get the DPoP key pair of the web session, generated and saved when missing. A web session is required.
     *
     * @param context the call context
     * @return the key pair
     */
    protected JWK getKey(final CallContext context) {
        assertSession(context);
        val webContext = context.webContext();
        val sessionStore = context.sessionStore();
        val saved = sessionStore.get(webContext, KEY_SESSION_ATTRIBUTE);
        if (saved.isPresent()) {
            try {
                return JWK.parse((String) saved.get());
            } catch (final ParseException e) {
                throw new TechnicalException("Cannot read the DPoP key from the session", e);
            }
        }
        val key = generateKey();
        LOGGER.debug("DPoP key pair generated and saved in the web session");
        sessionStore.set(webContext, KEY_SESSION_ATTRIBUTE, key.toJSONString());
        return key;
    }

    private void assertSession(final CallContext context) {
        if (context == null || context.webContext() == null || context.sessionStore() == null) {
            throw new TechnicalException("DPoP requires a web context and a session store to hold its key pair and nonces");
        }
    }

    /**
     * Generate a key pair for the signature algorithm.
     *
     * @return the key pair
     */
    protected JWK generateKey() {
        try {
            if (JWSAlgorithm.Family.EC.contains(jwsAlgorithm)) {
                return new ECKeyGenerator(Curve.forJWSAlgorithm(jwsAlgorithm).iterator().next()).generate();
            }
            if (JWSAlgorithm.Family.RSA.contains(jwsAlgorithm)) {
                return new RSAKeyGenerator(RSA_KEY_SIZE).generate();
            }
        } catch (final JOSEException e) {
            throw new TechnicalException("Cannot generate the DPoP key", e);
        }
        throw new TechnicalException("Unsupported DPoP signature algorithm: " + jwsAlgorithm);
    }
}
